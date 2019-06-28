package eki.ekilex.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserPermData;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.MaintenanceService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class PermissionsController extends AbstractPageController {

	private static final Logger logger = LoggerFactory.getLogger(PermissionsController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private MaintenanceService maintenanceService;

	@GetMapping(PERMISSIONS_URI)
	public String permissions(Model model) {
		EkiUser user = userService.getAuthenticatedUser();
		if (!user.isDatasetOwnershipExist()) {
			return "redirect:" + HOME_URI;
		}
		populateUserPermDataModel(model);
		return PERMISSIONS_PAGE;
	}

	@GetMapping(PERMISSIONS_URI + "/enable/{userId}")
	public String enable(@PathVariable("userId") Long userId, Model model) {
		userService.enableUser(userId, true);
		populateUserPermDataModel(model);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/disable/{userId}")
	public String disable(@PathVariable("userId") Long userId, Model model) {
		userService.enableUser(userId, false);
		populateUserPermDataModel(model);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/setadmin/{userId}")
	public String setAdmin(@PathVariable("userId") Long userId, Model model) {
		userService.setAdmin(userId, true);
		populateUserPermDataModel(model);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/remadmin/{userId}")
	public String remAdmin(@PathVariable("userId") Long userId, Model model) {
		userService.setAdmin(userId, false);
		populateUserPermDataModel(model);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@PostMapping(PERMISSIONS_URI + "/adddatasetperm")
	public String addDatasetPerm(
			@RequestParam("userId") Long userId,
			@RequestParam(value = "datasetCode", required = false) String datasetCode,
			@RequestParam(value = "authItem", required = false) AuthorityItem authItem,
			@RequestParam(value = "authOp", required = false) AuthorityOperation authOp,
			@RequestParam(value = "authLang", required = false) String authLang,
			Model model) {

		permissionService.createDatasetPermission(userId, datasetCode, authItem, authOp, authLang);

		return "redirect:" + PERMISSIONS_URI;
	}

	@GetMapping(PERMISSIONS_URI + "/deletedatasetperm/{datasetPermissionId}")
	public String deleteDatasetPerm(@PathVariable("datasetPermissionId") Long datasetPermissionId, Model model) {
		permissionService.deleteDatasetPermission(datasetPermissionId);
		populateUserPermDataModel(model);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	private void populateUserPermDataModel(Model model) {
		List<EkiUserPermData> ekiUserPermissions = permissionService.getEkiUserPermissions();
		model.addAttribute("ekiUserPermissions", ekiUserPermissions);
	}

	@ResponseBody
	@GetMapping(PERMISSIONS_URI + "/clearcache")
	public String clearCache() {
		maintenanceService.clearCache();
		return "OK";
	}

	@GetMapping(PERMISSIONS_URI + "/sendpermissionsemail/{userEmail}")
	@ResponseBody
	public String sendPermissionsEmail(@PathVariable("userEmail") String userEmail) {

		permissionService.sendPermissionsEmail(userEmail);
		return "OK";
	}

	@ResponseBody
	@GetMapping(PERMISSIONS_URI + "/dataset_languages/{datasetCode}")
	public String getDataSetLanguages(@PathVariable String datasetCode) throws Exception {

		List<Classifier> userLanguages = commonDataService.getDatasetLanguages(datasetCode);

		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(userLanguages);

	}
}
