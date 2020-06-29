package eki.ekilex.web.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityOperation;
import eki.common.constant.GlobalConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.EkiUserRoleData;
import eki.ekilex.service.UserContext;
import eki.ekilex.service.UserProfileService;
import eki.ekilex.web.bean.SessionBean;

@Component
public class UserProfileUtil implements GlobalConstant {

	private final List<AuthorityOperation> crudAuthOps = Arrays.asList(AuthorityOperation.CRUD, AuthorityOperation.OWN);

	@Autowired
	private UserContext userContext;

	@Autowired
	private UserProfileService userProfileService;

	public EkiUserProfile getUserProfile() {
		Long userId = userContext.getUserId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		return userProfile;
	}

	public EkiUserRoleData getUserRoleData(SessionBean sessionBean) {

		EkiUserRoleData ekiUserRoleData = new EkiUserRoleData();
		if (sessionBean == null) {
			 return ekiUserRoleData;
		}

		EkiUser user = userContext.getUser();
		DatasetPermission userRole = user.getRecentRole();

		boolean isAdmin = user.isAdmin();
		boolean isRoleSelected = userRole != null;
		boolean isCrudRoleSelected = isCrudRoleSelected(userRole);
		boolean isDatasetOwnerOrAdmin = isDatasetOwnerOrAdmin(user);
		boolean isDatasetCrudOwnerOrAdmin = isDatasetCrudOwnerOrAdmin(user);
		boolean isRoleChangeEnabled = isRoleChangeEnabled(user);

		ekiUserRoleData.setAdmin(isAdmin);
		ekiUserRoleData.setRoleSelected(isRoleSelected);
		ekiUserRoleData.setCrudRoleSelected(isCrudRoleSelected);
		ekiUserRoleData.setDatasetOwnerOrAdmin(isDatasetOwnerOrAdmin);
		ekiUserRoleData.setDatasetCrudOwnerOrAdmin(isDatasetCrudOwnerOrAdmin);
		ekiUserRoleData.setRoleChangeEnabled(isRoleChangeEnabled);
		return ekiUserRoleData;
	}

	private boolean isCrudRoleSelected(DatasetPermission userRole) {

		if (userRole == null) {
			return false;
		}
		AuthorityOperation authOperation = userRole.getAuthOperation();
		boolean isCrudRoleSelected = crudAuthOps.contains(authOperation);
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

}
