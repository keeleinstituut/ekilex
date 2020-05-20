package eki.ekilex.web.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.DatasetPermission;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserContext;
import eki.ekilex.web.bean.SessionBean;

@Component
public class PermDataUtil {

	@Autowired
	private UserContext userContext;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private LookupService lookupService;

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

	public boolean isSourceMeaningCrudGranted(Long sourceMeaningId, Long targetMeaningId, SessionBean sessionBean) {

		Long userId = userContext.getUserId();
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

	public boolean isWordValidForJoin(Long sourceWordId, Long targetWordId) {
		if (sourceWordId.equals(targetWordId)) {
			return false;
		}
		return lookupService.isValidWordStressAndMarkup(sourceWordId, targetWordId);
	}

	public boolean isSourceWordCrudGranted(Long sourceWordId, Long targetWordId, SessionBean sessionBean) {

		Long userId = userContext.getUserId();
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

		Long authenticatedUserId = userContext.getUserId();
		boolean isOwnPermission = authenticatedUserId.equals(userId);
		return isOwnPermission;
	}

}
