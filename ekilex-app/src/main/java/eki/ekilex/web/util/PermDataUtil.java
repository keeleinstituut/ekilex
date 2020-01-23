package eki.ekilex.web.util;

import java.util.List;

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

	public boolean isMeaningLexemeCrudGranted(Long meaningId, SessionBean sessionBean) {
		if (sessionBean == null) {
			return false;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
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
		boolean isSourceMeaningAnyLexemeCrudGranted = permissionService.isMeaningAnyLexemeCrudGranted(sourceMeaningId, userId);
		if (isSourceMeaningAnyLexemeCrudGranted) {
			return true;
		}

		if (sessionBean == null) {
			return false;
		}
		DatasetPermission userRole = sessionBean.getUserRole();
		if (userRole == null) {
			return false;
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
}
