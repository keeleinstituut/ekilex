package eki.ekilex.web.controller;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.constant.OrderingField;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserPermData;
import eki.ekilex.data.EkiUserRoleData;
import eki.ekilex.data.proto.PermPageInitModel;
import eki.ekilex.data.proto.PermPageSearchModel;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserService;
import eki.ekilex.service.core.UserContext;
import eki.ekilex.web.util.UserProfileUtil;

@PreAuthorize("@permEval.isPrivatePageAccessPermitted(authentication)")
@Controller
public class ProtoController2 implements WebConstant {

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserContext userContext;

	@Autowired
	private UserProfileUtil userProfileUtil;

	@PostMapping(PROTO_URI + PERMISSIONS_URI + INIT_URI)
	@ResponseBody
	public PermPageInitModel getPermPageInitModel() {

		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		EkiUserRoleData userRoleData = userProfileUtil.getUserRoleData(user);
		List<Dataset> userOwnedDatasets = permissionService.getUserOwnedDatasets(userId);
		List<AuthorityOperation> authorityOperations = Arrays.asList(AuthorityOperation.values());
		List<Classifier> languages = commonDataService.getLanguages();

		PermPageInitModel permPageInitModel = new PermPageInitModel();
		permPageInitModel.setUserRoleData(userRoleData);
		permPageInitModel.setUserOwnedDatasets(userOwnedDatasets);
		permPageInitModel.setAuthorityOperations(authorityOperations);
		permPageInitModel.setLanguages(languages);

		return permPageInitModel;
	}

	@PostMapping(PROTO_URI + PERMISSIONS_URI + SEARCH_URI)
	@ResponseBody
	public PermPageSearchModel permSearch(
			@RequestParam(name = "userNameFilter", required = false) String userNameFilter,
			@RequestParam(name = "userPermDatasetCodeFilter", required = false) String userPermDatasetCodeFilter,
			@RequestParam(name = "userEnablePendingFilter", required = false) Boolean userEnablePendingFilter,
			@RequestParam(name = "orderBy", required = false) OrderingField orderBy) {

		if (orderBy == null) {
			orderBy = OrderingField.NAME;
		}

		PermPageSearchModel permPageSearchModel = new PermPageSearchModel();
		permPageSearchModel.setUserNameFilter(userNameFilter);
		permPageSearchModel.setUserPermDatasetCodeFilter(userPermDatasetCodeFilter);
		permPageSearchModel.setUserEnablePendingFilter(userEnablePendingFilter);
		permPageSearchModel.setOrderBy(orderBy);

		if (StringUtils.isBlank(userNameFilter)
				&& StringUtils.isBlank(userPermDatasetCodeFilter)
				&& (userEnablePendingFilter == null)) {
			return permPageSearchModel;
		}

		final int minUserNameLength = 2;
		int userNameFilterLength = StringUtils.length(userNameFilter);

		if ((userNameFilterLength > 0) && (userNameFilterLength < minUserNameLength)) {
			return permPageSearchModel;
		}

		List<EkiUserPermData> ekiUserPermissions = permissionService.getEkiUserPermissions(userNameFilter, userPermDatasetCodeFilter, userEnablePendingFilter, orderBy);
		permPageSearchModel.setEkiUserPermissions(ekiUserPermissions);

		return permPageSearchModel;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(PROTO_URI + PERMISSIONS_URI + "/enable")
	@ResponseBody
	public String enable(@RequestParam("userId") Long userId) {

		userService.enableUser(userId, true);

		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(PROTO_URI + PERMISSIONS_URI + "/disable")
	@ResponseBody
	public String disable(@RequestParam("userId") Long userId) {

		userService.enableUser(userId, false);

		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(PROTO_URI + PERMISSIONS_URI + "/setapicrud")
	@ResponseBody
	public String setApiCrud(@RequestParam("userId") Long userId) {

		userService.setApiCrud(userId, true);

		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(PROTO_URI + PERMISSIONS_URI + "/remapicrud")
	@ResponseBody
	public String remApiCrud(@RequestParam("userId") Long userId) {

		userService.setApiCrud(userId, false);

		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(PROTO_URI + PERMISSIONS_URI + "/setadmin")
	@ResponseBody
	public String setAdmin(@RequestParam("userId") Long userId) {

		userService.setAdmin(userId, true);

		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(PROTO_URI + PERMISSIONS_URI + "/remadmin")
	@ResponseBody
	public String remAdmin(@RequestParam("userId") Long userId) {

		userService.setAdmin(userId, false);

		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(PROTO_URI + PERMISSIONS_URI + "/setmaster")
	@ResponseBody
	public String setMaster(@RequestParam("userId") Long userId) {

		userService.setMaster(userId, true);

		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(PROTO_URI + PERMISSIONS_URI + "/remmaster")
	@ResponseBody
	public String remMaster(@RequestParam("userId") Long userId) {

		userService.setMaster(userId, false);

		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("@permEval.isDatasetOwnerOrAdmin(authentication, #datasetCode)")
	@PostMapping(PROTO_URI + PERMISSIONS_URI + "/adddatasetperm")
	@ResponseBody
	public String addDatasetPerm(
			@RequestParam("userId") Long userId,
			@RequestParam("datasetCode") String datasetCode,
			@RequestParam("authOp") AuthorityOperation authOp,
			@RequestParam(name = "authLang", required = false) String authLang,
			@RequestParam(name = "userApplicationId", required = false) Long userApplicationId) {

		EkiUser permittedUser = userService.getUserById(userId);
		EkiUser permittingUser = userContext.getUser();
		permissionService.createDatasetPermission(permittedUser, permittingUser, datasetCode, AuthorityItem.DATASET, authOp, authLang);
		if (userApplicationId != null) {
			permissionService.approveApplication(userApplicationId, permittingUser);
		}

		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(PROTO_URI + PERMISSIONS_URI + "/deletedatasetperm")
	@ResponseBody
	public String deleteDatasetPerm(@RequestParam("datasetPermissionId") Long datasetPermissionId) {

		EkiUser user = userContext.getUser();
		permissionService.deleteDatasetPermission(datasetPermissionId, user);

		return RESPONSE_OK_VER1;
	}
}
