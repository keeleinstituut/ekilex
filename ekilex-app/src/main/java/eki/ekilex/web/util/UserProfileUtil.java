package eki.ekilex.web.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityOperation;
import eki.common.constant.PermConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.EkiUserRoleData;
import eki.ekilex.service.UserProfileService;
import eki.ekilex.service.core.UserContext;

@Component
public class UserProfileUtil implements PermConstant {

	@Autowired
	private UserContext userContext;

	@Autowired
	private UserProfileService userProfileService;

	public EkiUserProfile getUserProfile() {
		Long userId = userContext.getUserId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		return userProfile;
	}

	public EkiUserRoleData getUserRoleData() {

		EkiUserRoleData ekiUserRoleData = new EkiUserRoleData();

		EkiUser user = userContext.getUser();
		DatasetPermission userRole = user.getRecentRole();

		boolean isAdmin = user.isAdmin();
		boolean isRoleSelected = userRole != null;
		boolean isCrudRoleSelected = isCrudRoleSelected(userRole);
		boolean isDatasetOwnerOrAdmin = isDatasetOwnerOrAdmin(user);
		boolean isDatasetCrudOwnerOrAdmin = isDatasetCrudOwnerOrAdmin(user);
		boolean isRoleChangeEnabled = isRoleChangeEnabled(user);
		boolean isLexemeActiveTagChangeEnabled = isLexemeActiveTagChangeEnabled(userRole);

		ekiUserRoleData.setUserRole(userRole);
		ekiUserRoleData.setAdmin(isAdmin);
		ekiUserRoleData.setRoleSelected(isRoleSelected);
		ekiUserRoleData.setCrudRoleSelected(isCrudRoleSelected);
		ekiUserRoleData.setDatasetOwnerOrAdmin(isDatasetOwnerOrAdmin);
		ekiUserRoleData.setDatasetCrudOwnerOrAdmin(isDatasetCrudOwnerOrAdmin);
		ekiUserRoleData.setRoleChangeEnabled(isRoleChangeEnabled);
		ekiUserRoleData.setLexemeActiveTagChangeEnabled(isLexemeActiveTagChangeEnabled);
		return ekiUserRoleData;
	}

	private boolean isCrudRoleSelected(DatasetPermission userRole) {

		if (userRole == null) {
			return false;
		}
		AuthorityOperation authOperation = userRole.getAuthOperation();
		boolean isCrudRoleSelected = AUTH_OPS_CRUD.contains(authOperation.name());
		return isCrudRoleSelected;
	}

	private boolean isDatasetOwnerOrAdmin(EkiUser user) {

		boolean isDatasetOwner = user.isDatasetOwnershipExist();
		boolean isAdmin = user.isAdmin();
		return isDatasetOwner || isAdmin;
	}

	private boolean isDatasetCrudOwnerOrAdmin(EkiUser user) {

		boolean isDatasetCrudOwner = user.isDatasetCrudPermissionsExist();
		boolean isAdmin = user.isAdmin();
		return isDatasetCrudOwner || isAdmin;
	}

	private boolean isRoleChangeEnabled(EkiUser user) {

		boolean datasetPermissionsExist = user.isDatasetPermissionsExist();
		boolean hasMoreThanOnePermission = !user.isHasSingleDatasetPermission();
		return datasetPermissionsExist && hasMoreThanOnePermission;
	}

	private boolean isLexemeActiveTagChangeEnabled(DatasetPermission userRole) {

		if (userRole == null) {
			return false;
		}
		Long userId = userRole.getUserId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		String activeTagName = userProfile.getActiveTagName();
		return activeTagName != null;
	}

}
