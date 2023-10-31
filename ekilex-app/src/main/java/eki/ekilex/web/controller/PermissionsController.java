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
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.UserService;
import eki.ekilex.web.bean.PermSearchBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes({WebConstant.SESSION_BEAN, WebConstant.PERM_SEARCH_BEAN})
public class PermissionsController extends AbstractPrivatePageController {

	private static final OrderingField DEFAULT_ORDER_BY = OrderingField.NAME;

	@Autowired
	private UserService userService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private CommonDataService commonDataService;

	private PermSearchBean getPermSearchBean(Model model) {
		PermSearchBean permSearchBean = (PermSearchBean) model.asMap().get(PERM_SEARCH_BEAN);
		if (permSearchBean == null) {
			permSearchBean = new PermSearchBean();
			model.addAttribute(PERM_SEARCH_BEAN, permSearchBean);
		}
		return permSearchBean;
	}

	@GetMapping(PERMISSIONS_URI)
	public String permissions(Model model) {

		EkiUser user = userContext.getUser();
		if (!user.isDatasetOwnershipExist() && !user.isAdmin()) {
			return REDIRECT_PREF + HOME_URI;
		}

		PermSearchBean permFormBean = new PermSearchBean();
		permFormBean.setOrderBy(DEFAULT_ORDER_BY);
		model.addAttribute(PERM_SEARCH_BEAN, permFormBean);

		return PERMISSIONS_PAGE;
	}

	@PostMapping(PERMISSIONS_URI + SEARCH_URI)
	public String searchPost(
			@RequestParam(name = "userNameFilter", required = false) String userNameFilter,
			@RequestParam(name = "userPermDatasetCodeFilter", required = false) String userPermDatasetCodeFilter,
			@RequestParam(name = "userEnablePendingFilter", required = false) Boolean userEnablePendingFilter,
			Model model) {

		final int minUserNameLength = 2;
		int userNameFilterLength = StringUtils.length(userNameFilter);

		if (StringUtils.isBlank(userNameFilter)
				&& StringUtils.isBlank(userPermDatasetCodeFilter)
				&& (userEnablePendingFilter == null)) {
			return REDIRECT_PREF + PERMISSIONS_URI;
		}
		if (userNameFilterLength > 0 && userNameFilterLength < minUserNameLength) {
			return REDIRECT_PREF + PERMISSIONS_URI;
		}

		PermSearchBean permSearchBean = getPermSearchBean(model);
		permSearchBean.setUserNameFilter(userNameFilter);
		permSearchBean.setUserPermDatasetCodeFilter(userPermDatasetCodeFilter);
		permSearchBean.setUserEnablePendingFilter(userEnablePendingFilter);

		populateUserPermDataModel(model);

		return PERMISSIONS_PAGE;
	}

	@GetMapping(PERMISSIONS_URI + SEARCH_URI)
	public String searchGet(
			@ModelAttribute("userNameFilter") String userNameFilter,
			@ModelAttribute("userPermDatasetCodeFilter") String userPermDatasetCodeFilter,
			@ModelAttribute("userEnablePendingFilter") String userEnablePendingFilterStr,
			Model model) {

		if (StringUtils.isBlank(userNameFilter)
				&& StringUtils.isBlank(userPermDatasetCodeFilter)
				&& StringUtils.isBlank(userEnablePendingFilterStr)) {
			return REDIRECT_PREF + PERMISSIONS_URI;
		}
		if (StringUtils.length(userNameFilter) == 1) {
			return REDIRECT_PREF + PERMISSIONS_URI;
		}

		PermSearchBean permSearchBean = getPermSearchBean(model);
		permSearchBean.setUserNameFilter(userNameFilter);
		permSearchBean.setUserPermDatasetCodeFilter(userPermDatasetCodeFilter);
		permSearchBean.setUserEnablePendingFilter(Boolean.valueOf(userEnablePendingFilterStr));

		populateUserPermDataModel(model);

		return PERMISSIONS_PAGE;
	}

	@GetMapping(PERMISSIONS_URI + APPLICATIONS_URI + "/{datasetCode}")
	public String searchGet(@PathVariable("datasetCode") String datasetCode, Model model) {

		PermSearchBean permSearchBean = getPermSearchBean(model);
		permSearchBean.setUserPermDatasetCodeFilter(datasetCode);
		permSearchBean.setUserEnablePendingFilter(true);

		populateUserPermDataModel(model);

		return PERMISSIONS_PAGE;
	}

	@PostMapping(PERMISSIONS_URI + "/orderby")
	public String orderBy(@RequestParam("orderBy") OrderingField orderBy, Model model, RedirectAttributes redirectAttributes) {

		PermSearchBean permSearchBean = getPermSearchBean(model);
		permSearchBean.setOrderBy(orderBy);

		prepareSearchRedirect(permSearchBean, redirectAttributes);

		return REDIRECT_PREF + PERMISSIONS_URI + "/search";
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

	@GetMapping(PERMISSIONS_URI + "/setapicrud/{userId}")
	public String setApiCrud(@PathVariable("userId") Long userId, Model model) {

		userService.setApiCrud(userId, true);
		populateUserPermDataModel(model);

		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/remapicrud/{userId}")
	public String remApiCrud(@PathVariable("userId") Long userId, Model model) {

		userService.setApiCrud(userId, false);
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

	@GetMapping(PERMISSIONS_URI + "/setmaster/{userId}")
	public String setMaster(@PathVariable("userId") Long userId, Model model) {

		userService.setMaster(userId, true);
		populateUserPermDataModel(model);

		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/remmaster/{userId}")
	public String remMaster(@PathVariable("userId") Long userId, Model model) {

		userService.setMaster(userId, false);
		populateUserPermDataModel(model);

		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@PostMapping(PERMISSIONS_URI + "/adddatasetperm")
	public String addDatasetPerm(
			@RequestParam("userId") Long userId,
			@RequestParam("datasetCode") String datasetCode,
			@RequestParam("authOp") AuthorityOperation authOp,
			@RequestParam(name = "authLang", required = false) String authLang,
			@RequestParam(name = "userApplicationId", required = false) Long userApplicationId,
			Model model) {

		EkiUser permittedUser = userService.getUserById(userId);
		EkiUser permittingUser = userContext.getUser();
		permissionService.createDatasetPermission(permittedUser, permittingUser, datasetCode, AuthorityItem.DATASET, authOp, authLang);
		if (userApplicationId != null) {
			permissionService.approveApplication(userApplicationId, permittingUser);
		}
		populateUserPermDataModel(model);

		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/deletedatasetperm/{datasetPermissionId}")
	public String deleteDatasetPerm(
			@PathVariable("datasetPermissionId") Long datasetPermissionId,
			Model model) {

		EkiUser user = userContext.getUser();
		permissionService.deleteDatasetPermission(datasetPermissionId, user);
		populateUserPermDataModel(model);

		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/rejectapplication/{userApplicationId}")
	public String rejectApplication(
			@PathVariable("userApplicationId") Long userApplicationId,
			Model model) {

		EkiUser user = userContext.getUser();
		permissionService.rejectApplication(userApplicationId, user);
		populateUserPermDataModel(model);

		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@PostMapping(PERMISSIONS_URI + "/updatereviewcomment")
	public String updateReviewComment(
			@RequestParam("userId") Long userId,
			@RequestParam("reviewComment") String reviewComment,
			Model model) {

		userService.updateReviewComment(userId, reviewComment);
		populateUserPermDataModel(model);

		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/deletereviewcomment/{userId}")
	public String deleteReviewComment(
			@PathVariable("userId") Long userId,
			Model model) {

		userService.updateReviewComment(userId, null);
		populateUserPermDataModel(model);

		return PERMISSIONS_PAGE + PAGE_FRAGMENT_ELEM + "permissions";
	}

	@GetMapping(PERMISSIONS_URI + "/sendpermissionsemail/{userEmail}")
	@ResponseBody
	public String sendPermissionsEmail(@PathVariable("userEmail") String userEmail) {

		EkiUser sender = userContext.getUser();
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

	private void populateUserPermDataModel(Model model) {

		PermSearchBean permSearchBean = getPermSearchBean(model);
		String userNameFilter = permSearchBean.getUserNameFilter();
		String userPermDatasetCodeFilter = permSearchBean.getUserPermDatasetCodeFilter();
		Boolean userEnablePendingFilter = permSearchBean.getUserEnablePendingFilter();
		OrderingField orderBy = permSearchBean.getOrderBy();

		List<EkiUserPermData> ekiUserPermissions = permissionService.getEkiUserPermissions(
				userNameFilter, userPermDatasetCodeFilter, userEnablePendingFilter, orderBy);
		model.addAttribute("ekiUserPermissions", ekiUserPermissions);
	}

	private void prepareSearchRedirect(PermSearchBean permSearchBean, RedirectAttributes redirectAttributes) {

		String userNameFilter = permSearchBean.getUserNameFilter();
		String userPermDatasetCodeFilter = permSearchBean.getUserPermDatasetCodeFilter();
		Boolean userEnablePendingFilter = permSearchBean.getUserEnablePendingFilter();
		OrderingField orderBy = permSearchBean.getOrderBy();

		if (StringUtils.isNotBlank(userNameFilter)) {
			redirectAttributes.addFlashAttribute("userNameFilter", userNameFilter);
		}
		if (StringUtils.isNotBlank(userPermDatasetCodeFilter)) {
			redirectAttributes.addFlashAttribute("userPermDatasetCodeFilter", userPermDatasetCodeFilter);
		}
		if (userEnablePendingFilter != null) {
			redirectAttributes.addFlashAttribute("userEnablePendingFilter", userEnablePendingFilter);
		}
		redirectAttributes.addFlashAttribute("orderBy", orderBy);
	}
}
