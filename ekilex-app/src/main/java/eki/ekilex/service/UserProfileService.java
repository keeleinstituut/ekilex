package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.UserProfileDbService;

@Component
public class UserProfileService {

	@Autowired
	private UserProfileDbService userProfileDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Transactional
	public EkiUserProfile getUserProfile(Long userId) {
		return userProfileDbService.getUserProfile(userId);
	}

	@Transactional
	public void updateUserPreferredDatasets(List<String> selectedDatasets, Long userId) {
		userProfileDbService.updatePreferredDatasets(selectedDatasets, userId);
	}

	@Transactional
	public void updateUserPreferredBilingCandidateLangs(List<String> languages, Long userId) {
		userProfileDbService.updatePreferredBilingCandidateLangs(languages, userId);
	}

	@Transactional
	public void updateUserPreferredMeaningWordLangs(List<String> languages, Long userId) {
		userProfileDbService.updatePreferredMeaningWordLangs(languages, userId);
	}

	@Transactional
	public void updateUserPreferredMeaningRelationWordLangs(List<String> languages, Long userId) {
		userProfileDbService.updatePreferredMeaningRelationWordLangs(languages, userId);
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
