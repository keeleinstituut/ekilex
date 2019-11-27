package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.constant.OrderingField;
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

	private static final OrderingField DEFAULT_ORDER_BY = OrderingField.NAME;

	@Autowired
	private UserService userService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private MaintenanceService maintenanceService;

	@ResponseBody
	@GetMapping(PERMISSIONS_URI + "/clearcache")
	public String clearCache() {

		maintenanceService.clearCache();

		return RESPONSE_OK_VER1;
	}

	@GetMapping(PERMISSIONS_URI)
	public String permissions(@ModelAttribute("orderBy") String orderByStr, Model model) {

		OrderingField orderBy = null;
		if (StringUtils.isNotBlank(orderByStr)) {
			orderBy = OrderingField.valueOf(orderByStr);
		}
		EkiUser user = userService.getAuthenticatedUser();
		if (!user.isDatasetOwnershipExist() && !user.isAdmin()) {
			return "redirect:" + HOME_URI;
		}
		populateUserPermDataModel(model, orderBy);
		return PERMISSIONS_PAGE;
	}

	@PostMapping(PERMISSIONS_URI)
	public String permissions(@RequestParam("orderBy") OrderingField orderBy, RedirectAttributes attributes) {

		attributes.addFlashAttribute("orderBy", orderBy);
		return "redirect:" + PERMISSIONS_URI;
	}

	@GetMapping(PERMISSIONS_URI + "/enable/{userId}/{orderBy}")
	public String enable(@PathVariable("userId") Long userId, @PathVariable("orderBy") OrderingField orderBy, Model model) {

		userService.enableUser(userId, true);
		populateUserPermDataModel(model, orderBy);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/disable/{userId}/{orderBy}")
	public String disable(@PathVariable("userId") Long userId, @PathVariable("orderBy") OrderingField orderBy, Model model) {

		userService.enableUser(userId, false);
		populateUserPermDataModel(model, orderBy);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/setadmin/{userId}/{orderBy}")
	public String setAdmin(@PathVariable("userId") Long userId, @PathVariable("orderBy") OrderingField orderBy, Model model) {

		userService.setAdmin(userId, true);
		populateUserPermDataModel(model, orderBy);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/remadmin/{userId}/{orderBy}")
	public String remAdmin(@PathVariable("userId") Long userId, @PathVariable("orderBy") OrderingField orderBy, Model model) {

		userService.setAdmin(userId, false);
		populateUserPermDataModel(model, orderBy);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/setreviewed/{userId}/{orderBy}")
	public String setReviewed(@PathVariable("userId") Long userId, @PathVariable("orderBy") OrderingField orderBy, Model model) {

		userService.setReviewed(userId, true);
		populateUserPermDataModel(model, orderBy);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/remreviewed/{userId}/{orderBy}")
	public String remReviewed(@PathVariable("userId") Long userId, @PathVariable("orderBy") OrderingField orderBy, Model model) {

		userService.setReviewed(userId, false);
		populateUserPermDataModel(model, orderBy);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@PostMapping(PERMISSIONS_URI + "/adddatasetperm")
	public String addDatasetPerm(
			@RequestParam("userId") Long userId,
			@RequestParam(value = "datasetCode", required = false) String datasetCode,
			@RequestParam(value = "authItem", required = false) AuthorityItem authItem,
			@RequestParam(value = "authOp", required = false) AuthorityOperation authOp,
			@RequestParam(value = "authLang", required = false) String authLang,
			@RequestParam("orderBy") OrderingField orderBy,
			RedirectAttributes attributes) {

		permissionService.createDatasetPermission(userId, datasetCode, authItem, authOp, authLang);
		attributes.addFlashAttribute("orderBy", orderBy);
		return "redirect:" + PERMISSIONS_URI;
	}

	@GetMapping(PERMISSIONS_URI + "/deletedatasetperm/{datasetPermissionId}/{orderBy}")
	public String deleteDatasetPerm(@PathVariable("datasetPermissionId") Long datasetPermissionId, @PathVariable("orderBy") OrderingField orderBy,
			Model model) {

		permissionService.deleteDatasetPermission(datasetPermissionId);
		populateUserPermDataModel(model, orderBy);
		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@PostMapping(PERMISSIONS_URI + "/updatereviewcomment")
	public String updateReviewComment(@RequestParam("userId") Long userId, @RequestParam("reviewComment") String reviewComment,
			@RequestParam("orderBy") OrderingField orderBy, RedirectAttributes attributes) {

		userService.updateReviewComment(userId, reviewComment);
		attributes.addFlashAttribute("orderBy", orderBy);
		return "redirect:" + PERMISSIONS_URI;
	}

	@GetMapping(PERMISSIONS_URI + "/deletereviewcomment/{userId}/{orderBy}")
	public String deleteReviewComment(@PathVariable("userId") Long userId, @PathVariable("orderBy") OrderingField orderBy, RedirectAttributes attributes) {

		userService.updateReviewComment(userId, null);
		attributes.addFlashAttribute("orderBy", orderBy);
		return "redirect:" + PERMISSIONS_URI;
	}

	@GetMapping(PERMISSIONS_URI + "/sendpermissionsemail/{userEmail}")
	@ResponseBody
	public String sendPermissionsEmail(@PathVariable("userEmail") String userEmail) {

		EkiUser sender = userService.getAuthenticatedUser();
		permissionService.sendPermissionsEmail(userEmail, sender);

		return RESPONSE_OK_VER1;
	}

	@ResponseBody
	@GetMapping(PERMISSIONS_URI + "/dataset_languages/{datasetCode}")
	public String getDataSetLanguages(@PathVariable String datasetCode) throws Exception {

		List<Classifier> userLanguages = commonDataService.getDatasetLanguages(datasetCode);

		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(userLanguages);
	}

	private void populateUserPermDataModel(Model model, OrderingField orderBy) {

		if (orderBy == null) {
			orderBy = DEFAULT_ORDER_BY;
		}
		List<EkiUserPermData> ekiUserPermissions = permissionService.getEkiUserPermissions(orderBy);
		model.addAttribute("ekiUserPermissions", ekiUserPermissions);
		model.addAttribute("orderBy", orderBy);
	}
}
