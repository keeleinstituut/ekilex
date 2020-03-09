package eki.ekilex.web.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityOperation;
import eki.common.constant.LayerName;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserProfileService;
import eki.ekilex.service.UserService;
import eki.ekilex.web.bean.SessionBean;

@Component
public class PermDataUtil implements SystemConstant {

	private final List<AuthorityOperation> crudAuthOps = Arrays.asList(AuthorityOperation.CRUD, AuthorityOperation.OWN);

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserProfileService userProfileService;

	public boolean isRoleSelected(SessionBean sessionBean) {
		if (sessionBean == null) {
			return false;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		boolean isRoleSelected = userRole != null;
		return isRoleSelected;
	}

	public boolean isRoleSelected(SessionBean sessionBean, DatasetPermission datasetPermission) {
		if (sessionBean == null) {
			return false;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return false;
		}
		boolean isRoleSelected = userRole.getId().equals(datasetPermission.getId());
		return isRoleSelected;
	}

	public boolean isCrudRoleSelected(SessionBean sessionBean) {
		if (sessionBean == null) {
			return false;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return false;
		}
		AuthorityOperation authOperation = userRole.getAuthOperation();
		boolean isCrudRoleSelected = crudAuthOps.contains(authOperation);
		return isCrudRoleSelected;
	}

	public boolean isMeaningLexemeCrudGranted(Long meaningId, SessionBean sessionBean) {
		if (sessionBean == null) {
			return false;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return false;
		}
		AuthorityOperation authOperation = userRole.getAuthOperation();
		if (!crudAuthOps.contains(authOperation)) {
			return false;
		}
		String datasetCode = userRole.getDatasetCode();
		boolean datasetExists = permissionService.meaningDatasetExists(meaningId, datasetCode);
		return datasetExists;
	}

	public boolean isWordLexemeCrudGranted(Long wordId, SessionBean sessionBean) {
		if (sessionBean == null) {
			return false;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return false;
		}
		AuthorityOperation authOperation = userRole.getAuthOperation();
		if (!crudAuthOps.contains(authOperation)) {
			return false;
		}
		String datasetCode = userRole.getDatasetCode();
		boolean datasetExists = permissionService.wordDatasetExists(wordId, datasetCode);
		return datasetExists;
	}

	public boolean isMeaningAnyLexemeCrudGranted(Long meaningId) {
		Long userId = userService.getAuthenticatedUser().getId();
		return permissionService.isMeaningAnyLexemeCrudGranted(meaningId, userId);
	}

	public boolean isSourceMeaningCrudGranted(Long sourceMeaningId, Long targetMeaningId, SessionBean sessionBean) {

		Long userId = userService.getAuthenticatedUser().getId();
		if (sessionBean == null) {
			return false;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return false;
		}

		boolean isSourceMeaningAnyLexemeCrudGranted = permissionService.isMeaningAnyLexemeCrudGranted(sourceMeaningId, userId);
		if (isSourceMeaningAnyLexemeCrudGranted) {
			return true;
		}

		String roleDatasetCode = userRole.getDatasetCode();
		boolean isRoleDatasetSuperior = userRole.isSuperiorDataset();
		boolean targetMeaningHasSuperiorLexemes = false;
		if (isRoleDatasetSuperior) {
			targetMeaningHasSuperiorLexemes = permissionService.meaningDatasetExists(targetMeaningId, roleDatasetCode);
		}
		return targetMeaningHasSuperiorLexemes;
	}

	public boolean isSourceWordCrudGranted(Long sourceWordId, Long targetWordId, SessionBean sessionBean) {

		Long userId = userService.getAuthenticatedUser().getId();
		if (sessionBean == null) {
			return false;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return false;
		}
		String roleDatasetCode = userRole.getDatasetCode();
		List<String> userPermDatasetCodes = permissionService.getUserPermDatasetCodes(userId);

		boolean isSourceWordCrudGranted = permissionService.isGrantedForWord(sourceWordId, roleDatasetCode, userPermDatasetCodes);
		if (isSourceWordCrudGranted) {
			return true;
		}

		boolean isRoleDatasetSuperior = userRole.isSuperiorDataset();
		boolean targetWordHasSuperiorLexemes = false;
		if (isRoleDatasetSuperior) {
			targetWordHasSuperiorLexemes = permissionService.wordDatasetExists(targetWordId, roleDatasetCode);
		}
		return targetWordHasSuperiorLexemes;
	}

	public boolean isOwnPermission(Long userId) {

		Long authenticatedUserId = userService.getAuthenticatedUser().getId();
		boolean isOwnPermission = authenticatedUserId.equals(userId);
		return isOwnPermission;
	}

	public boolean isAdmin() {

		boolean isAdmin = userService.getAuthenticatedUser().isAdmin();
		return isAdmin;
	}

	public boolean isDatasetOwnerOrAdmin() {

		EkiUser user = userService.getAuthenticatedUser();
		boolean isDatasetOwner = user.isDatasetOwnershipExist();
		boolean isAdmin = user.isAdmin();
		return isDatasetOwner || isAdmin;
	}

	public boolean isDatasetCrudOwnerOrAdmin() {

		EkiUser user = userService.getAuthenticatedUser();
		boolean isDatasetCrudOwner = user.isDatasetCrudPermissionsExist();
		boolean isAdmin = user.isAdmin();
		return isDatasetCrudOwner || isAdmin;
	}

	public boolean isRoleChangeEnabled() {

		EkiUser user = userService.getAuthenticatedUser();
		boolean datasetPermissionsExist = user.isDatasetPermissionsExist();
		boolean hasMoreThanOnePermission = !user.isHasSingleDatasetPermission();
		return datasetPermissionsExist && hasMoreThanOnePermission;
	}

	public boolean isLayerChangeEnabled(SessionBean sessionBean) {

		if (sessionBean == null) {
			return false;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return false;
		}
		return StringUtils.equals(userRole.getDatasetCode(), DATASET_SSS);
	}

	public boolean isProcessStateChangeEnabled(SessionBean sessionBean) {

		boolean isLayerChangeEnabled = isLayerChangeEnabled(sessionBean);
		if (!isLayerChangeEnabled) {
			return false;
		}
		Long userId = userService.getAuthenticatedUser().getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		LayerName layerName = userProfile.getPreferredLayerName();
		return !LayerName.NONE.equals(layerName);
	}
}
