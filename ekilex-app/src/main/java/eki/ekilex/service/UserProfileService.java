package eki.ekilex.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.LayerName;
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
			LayerName layerName = userProfile.getPreferredLayerName();
			List<String> synCandidateLangs = userProfile.getPreferredSynCandidateLangs();
			List<String> synLexMeaningWordLangs = userProfile.getPreferredSynLexMeaningWordLangs();
			List<String> meaningRelationWordLangs = userProfile.getPreferredMeaningRelationWordLangs();
			List<Classifier> allLangs = commonDataDbService.getLanguages(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<String> allLangCodes = allLangs.stream().map(Classifier::getCode).collect(Collectors.toList());

			if (layerName == null) {
				layerName = LayerName.NONE;
				userProfile.setPreferredLayerName(layerName);
			}
			if (CollectionUtils.isEmpty(synCandidateLangs)) {
				synCandidateLangs = allLangCodes;
				userProfile.setPreferredSynCandidateLangs(synCandidateLangs);
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
	public void updateUserPreferredSynCandidateLangs(List<String> languages, Long userId) {
		userProfileDbService.updatePreferredSynCandidateLangs(languages, userId);
	}

	@Transactional
	public void updateUserPreferredMeaningWordLangs(List<String> languages, Long userId) {
		userProfileDbService.updatePreferredMeaningWordLangs(languages, userId);
	}

	@Transactional
	public void updateUserPreferredLayerName(LayerName layerName, Long userId) {
		userProfileDbService.updateUserPreferredLayerName(layerName, userId);
	}

	@Transactional
	public void updateUserProfile(EkiUserProfile userProfile) {
		userProfileDbService.updateUserProfile(userProfile);
	}

	@Transactional
	public DatasetPermission getAndSetRecentDatasetPermission(Long permissionId, Long userId) {

		DatasetPermission datasetPermission = permissionDbService.getDatasetPermission(permissionId);
		if (datasetPermission == null) {
			userProfileDbService.setRecentDatasetPermission(userId, null);
		} else {
			userProfileDbService.setRecentDatasetPermission(userId, permissionId);
		}
		return datasetPermission;
	}

}
