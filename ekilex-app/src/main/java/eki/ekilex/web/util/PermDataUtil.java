package eki.ekilex.web.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityOperation;
import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserService;
import eki.ekilex.web.bean.SessionBean;

@Component
public class PermDataUtil implements SystemConstant, GlobalConstant {

	private final List<AuthorityOperation> crudAuthOps = Arrays.asList(AuthorityOperation.CRUD, AuthorityOperation.OWN);

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private LookupService lookupService;

	@Autowired
	private UserService userService;

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

		boolean isValidWordStressAndMarkup = lookupService.isValidWordStressAndMarkup(targetWordId, sourceWordId);
		if (!isValidWordStressAndMarkup) {
			return false;
		}

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

	public boolean isMeaningOtherLexemesDeleteGranted(String wordLang) {
		return !StringUtils.equals(LANGUAGE_CODE_EST, wordLang);
	}

	public boolean isOwnPermission(Long userId) {

		Long authenticatedUserId = userService.getAuthenticatedUser().getId();
		boolean isOwnPermission = authenticatedUserId.equals(userId);
		return isOwnPermission;
	}

}
