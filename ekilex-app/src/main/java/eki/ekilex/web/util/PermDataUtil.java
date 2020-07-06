package eki.ekilex.web.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.PermissionGrantService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserContext;

@Component
public class PermDataUtil {

	@Autowired
	private UserContext userContext;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private PermissionGrantService permissionGrantService;

	@Autowired
	private LookupService lookupService;

	public boolean isRoleSelected(DatasetPermission datasetPermission) {

		EkiUser user = userContext.getUser();
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return false;
		}
		boolean isRoleSelected = userRole.getId().equals(datasetPermission.getId());
		return isRoleSelected;
	}

	public boolean isSourceMeaningCrudGranted(Long sourceMeaningId, Long targetMeaningId) {

		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return false;
		}

		boolean isSourceMeaningAnyLexemeCrudGranted = permissionGrantService.isMeaningAnyLexemeCrudGranted(userId, sourceMeaningId);
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

	public boolean isSourceWordCrudGranted(Long sourceWordId, Long targetWordId) {

		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return false;
		}
		String roleDatasetCode = userRole.getDatasetCode();
		boolean isSourceWordCrudGranted = permissionGrantService.isWordCrudGranted(userId, sourceWordId, roleDatasetCode);
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
