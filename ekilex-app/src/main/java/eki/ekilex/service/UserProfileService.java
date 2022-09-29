package eki.ekilex.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.DatasetPermission;
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
			List<Classifier> allLangs = commonDataDbService.getLanguages(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
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
	public void updateUserPreferredDatasets(List<String> selectedDatasets, Long userId) {
		userProfileDbService.updatePreferredDatasets(selectedDatasets, userId);
	}

	@Transactional
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

	@Transactional
	public void updateApproveMeaningEnabled(Long userId, boolean approveMeaningEnabled) {

		userProfileDbService.updateApproveMeaningEnabled(userId, approveMeaningEnabled);
	}

	@Transactional
	public void updateUserProfile(EkiUserProfile userProfile) {
		userProfileDbService.updateUserProfile(userProfile);
	}
}
