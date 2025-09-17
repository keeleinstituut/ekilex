package eki.ekilex.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ClassifierName;
import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.UserProfileDbService;

@Component
public class UserProfileService implements GlobalConstant, SystemConstant {

	@Autowired
	private UserProfileDbService userProfileDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Transactional
	public EkiUserProfile getUserProfile(Long userId) {

		EkiUserProfile userProfile = userProfileDbService.getUserProfile(userId);

		if (userProfile != null) {
			List<String> partSynCandidateLangs = userProfile.getPreferredPartSynCandidateLangs();
			List<String> synLexMeaningWordLangs = userProfile.getPreferredSynLexMeaningWordLangs();
			List<String> meaningRelationWordLangs = userProfile.getPreferredMeaningRelationWordLangs();
			List<Classifier> allLangs = commonDataDbService.getDefaultClassifiers(ClassifierName.LANGUAGE, CLASSIF_LABEL_LANG_EST);
			List<String> allLangCodes = allLangs.stream().map(Classifier::getCode).collect(Collectors.toList());

			if (CollectionUtils.isEmpty(partSynCandidateLangs)) {
				partSynCandidateLangs = allLangCodes;
				userProfile.setPreferredPartSynCandidateLangs(partSynCandidateLangs);
			}
			if (CollectionUtils.isEmpty(synLexMeaningWordLangs)) {
				synLexMeaningWordLangs = allLangCodes;
				userProfile.setPreferredSynLexMeaningWordLangs(synLexMeaningWordLangs);
			}
			if (CollectionUtils.isEmpty(meaningRelationWordLangs)) {
				meaningRelationWordLangs = Collections.singletonList(LANGUAGE_CODE_EST);
				userProfile.setPreferredMeaningRelationWordLangs(meaningRelationWordLangs);
			}
		}

		return userProfile;
	}

	@Transactional
	public List<Classifier> getUserRoleLanguagesExtended(EkiUser user) {

		if (user.isMaster()) {
			return commonDataDbService.getDefaultClassifiers(ClassifierName.LANGUAGE, CLASSIF_LABEL_LANG_EST);
		}
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return Collections.emptyList();
		}

		Long userId = user.getId();
		String userRoleDatasetCode = userRole.getDatasetCode();
		String userRoleAuthLang = userRole.getAuthLang();

		List<Classifier> userPermLanguages = permissionDbService.getUserDatasetLanguages(userId, userRoleDatasetCode, CLASSIF_LABEL_LANG_EST);
		List<Classifier> datasetLanguages = commonDataDbService.getDatasetClassifiers(ClassifierName.LANGUAGE, userRoleDatasetCode, CLASSIF_LABEL_LANG_EST);

		if (StringUtils.isNotBlank(userRoleAuthLang)) {
			datasetLanguages = datasetLanguages.stream()
					.filter(classifier -> StringUtils.equals(classifier.getCode(), userRoleAuthLang))
					.collect(Collectors.toList());
		}
		List<Classifier> userAvailableLanguages = userPermLanguages.stream()
				.filter(datasetLanguages::contains)
				.collect(Collectors.toList());

		return userAvailableLanguages;
	}

	@Transactional
	public List<Classifier> getUserRoleLanguagesLimited(EkiUser user) {

		List<Classifier> userAvailableLanguages = getUserRoleLanguagesExtended(user);
		if (CollectionUtils.isNotEmpty(userAvailableLanguages)) {
			userAvailableLanguages = userAvailableLanguages.stream()
					.filter(classifier -> !StringUtils.equals(classifier.getCode(), lANGUAGE_CODE_MUL))
					.collect(Collectors.toList());
		}
		return userAvailableLanguages;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateUserPreferredDatasets(List<String> selectedDatasets, Long userId) {
		userProfileDbService.updatePreferredDatasets(selectedDatasets, userId);
	}

	@Transactional(rollbackFor = Exception.class)
	public void setRecentDatasetPermission(Long permissionId, Long userId) {

		if (permissionId == null) {
			userProfileDbService.setRecentDatasetPermission(userId, null);
		} else {
			DatasetPermission datasetPermission = permissionDbService.getDatasetPermission(permissionId);
			if (datasetPermission == null) {
				userProfileDbService.setRecentDatasetPermission(userId, null);
			} else {
				userProfileDbService.setRecentDatasetPermission(userId, permissionId);
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateApproveMeaningEnabled(Long userId, boolean approveMeaningEnabled) {

		userProfileDbService.updateApproveMeaningEnabled(userId, approveMeaningEnabled);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateUserProfile(EkiUserProfile userProfile) {
		userProfileDbService.updateUserProfile(userProfile);
	}

}
