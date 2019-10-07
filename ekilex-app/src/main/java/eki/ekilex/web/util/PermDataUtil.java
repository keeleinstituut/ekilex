package eki.ekilex.web.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.DatasetPermission;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserService;
import eki.ekilex.web.bean.SessionBean;

@Component
public class PermDataUtil {

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	private UserService userService;

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

	public String getRoleDatasetCode(SessionBean sessionBean) {
		if (sessionBean == null) {
			return null;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return null;
		}
		return userRole.getDatasetCode();
	}

	public boolean isLexemeDeleteGrantedForMeaning(Long meaningId, SessionBean sessionBean) {
		if (sessionBean == null) {
			return false;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return false;
		}
		String datasetCode = userRole.getDatasetCode();
		boolean isGrantedForMeaning = permissionService.isGrantedForMeaning(meaningId, datasetCode);
		return isGrantedForMeaning;
	}

	public boolean isMeaningAnyLexemeCrudGranted(Long meaningId) {
		Long userId = userService.getAuthenticatedUser().getId();
		return permissionService.isMeaningAnyLexemeCrudGranted(meaningId, userId);
	}
}
