package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.common.constant.AuthorityOperation;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserApplication;
import eki.ekilex.data.NewsArticle;
import eki.ekilex.data.StatData;
import eki.ekilex.data.StatDataRow;
import eki.ekilex.security.EkilexPermissionEvaluator;
import eki.ekilex.service.NewsService;
import eki.ekilex.service.StatDataService;
import eki.ekilex.service.UserProfileService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class HomeController extends AbstractPublicPageController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private StatDataService statDataService;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private NewsService newsService;

	@Autowired
	private EkilexPermissionEvaluator permissionEvaluator;

	@GetMapping(INDEX_URI)
	public String index() {
		boolean isAuthenticatedUser = userContext.isAuthenticatedUser();
		if (isAuthenticatedUser) {
			return REDIRECT_PREF + HOME_URI;
		}
		return "index";
	}

	@GetMapping(HOME_URI)
	public String home(Authentication authentication, Model model) throws Exception {

		boolean isActiveTermsAgreed = permissionEvaluator.isActiveTermsAgreed(authentication);
		if (!isActiveTermsAgreed) {
			return REDIRECT_PREF + TERMS_AGREEMENT_PAGE_URI;
		}
		boolean isPrivatePageAccessPermitted = permissionEvaluator.isPrivatePageAccessPermitted(authentication);
		if (isPrivatePageAccessPermitted) {
			populateNewsArticlesModel(model);
			populateStatDataModel(model);
			return HOME_PAGE;
		}
		boolean isLimitedPageAccessPermitted = permissionEvaluator.isLimitedPageAccessPermitted(authentication);
		if (isLimitedPageAccessPermitted) {
			return REDIRECT_PREF + LIM_TERM_SEARCH_URI;
		}
		EkiUser user = userContext.getUser();
		populateUserApplicationData(user, model);
		return APPLY_PAGE;
	}

	@GetMapping(LOGIN_PAGE_URI)
	public String login() {
		return LOGIN_PAGE;
	}

	@GetMapping(APPLY_URI)
	public String apply(Model model) {

		EkiUser user = userContext.getUser();
		boolean isUserEnabled = Boolean.TRUE.equals(user.getEnabled());
		boolean datasetPermissionsExist = user.isDatasetPermissionsExist();
		if (isUserEnabled && datasetPermissionsExist) {
			return REDIRECT_PREF + HOME_URI;
		}
		populateUserApplicationData(user, model);

		return APPLY_PAGE;
	}

	@PostMapping(APPLY_URI)
	public String apply(
			@RequestParam("datasetCode") String datasetCode,
			@RequestParam("authOp") AuthorityOperation authOp,
			@RequestParam("language") String lang,
			@RequestParam("applicationComment") String applicationComment,
			Model model) {

		EkiUser user = userContext.getUser();
		boolean isUserEnabled = Boolean.TRUE.equals(user.getEnabled());
		boolean datasetPermissionsExist = user.isDatasetPermissionsExist();
		if (isUserEnabled && datasetPermissionsExist) {
			populateStatDataModel(model);
			return HOME_PAGE;
		}

		userService.submitUserApplication(user, datasetCode, authOp, lang, applicationComment);
		populateUserApplicationData(user, model);

		return REDIRECT_PREF + HOME_URI;
	}

	@PostMapping(APPLY_READ)
	public String applyRead(Model model) {

		EkiUser user = userContext.getUser();
		boolean isUserEnabled = Boolean.TRUE.equals(user.getEnabled());
		boolean datasetPermissionsExist = user.isDatasetPermissionsExist();
		if (isUserEnabled && datasetPermissionsExist) {
			populateStatDataModel(model);
			return HOME_PAGE;
		}
		Long userId = user.getId();
		userService.enableUserWithTestAndLimitedDatasetPerm(userId);
		return REDIRECT_PREF + HOME_PAGE;
	}

	@PostMapping(APPLY_LIMITED_URI)
	public String applyLimited(Model model) {

		EkiUser user = userContext.getUser();
		boolean isUserEnabled = Boolean.TRUE.equals(user.getEnabled());
		boolean datasetPermissionsExist = user.isDatasetPermissionsExist();
		if (isUserEnabled && datasetPermissionsExist) {
			populateStatDataModel(model);
			return HOME_PAGE;
		}
		Long userId = user.getId();
		userService.enableLimitedUser(userId);
		return REDIRECT_PREF + LIM_TERM_SEARCH_URI;
	}

	private void populateUserApplicationData(EkiUser user, Model model) {

		List<EkiUserApplication> userApplications = userService.getUserApplications(user.getId());
		boolean applicationNotSubmitted = CollectionUtils.isEmpty(userApplications);
		boolean applicationReviewPending = (user.getEnabled() == null) && CollectionUtils.isNotEmpty(userApplications);
		boolean applicationDenied = Boolean.FALSE.equals(user.getEnabled());

		model.addAttribute("applicationNotSubmitted", applicationNotSubmitted);
		model.addAttribute("applicationReviewPending", applicationReviewPending);
		model.addAttribute("applicationDenied", applicationDenied);
		model.addAttribute("userApplications", userApplications);
	}

	private void populateStatDataModel(Model model) {

		StatData mainEntityStatData = statDataService.getMainEntityStatData();
		List<StatDataRow> freeformStatData = statDataService.getFreeformStatData();
		List<StatDataRow> lexemeDatasetStatData = statDataService.getLexemeDatasetStatData();
		List<StatDataRow> activityStatData = statDataService.getActivityStatData();
		List<StatDataRow> apiRequestStatData = statDataService.getApiRequestStatData();
		List<StatDataRow> apiErrorStatData = statDataService.getApiErrorStatData();
		boolean statExists = (mainEntityStatData.getDatasetCount() > 0)
				&& CollectionUtils.isNotEmpty(freeformStatData)
				&& CollectionUtils.isNotEmpty(lexemeDatasetStatData)
				&& CollectionUtils.isNotEmpty(activityStatData);

		model.addAttribute("mainEntityStatData", mainEntityStatData);
		model.addAttribute("freeformStatData", freeformStatData);
		model.addAttribute("lexemeDatasetStatData", lexemeDatasetStatData);
		model.addAttribute("activityStatData", activityStatData);
		model.addAttribute("apiRequestStatData", apiRequestStatData);
		model.addAttribute("apiErrorStatData", apiErrorStatData);
		model.addAttribute("statExists", statExists);
	}

	private void populateNewsArticlesModel(Model model) {
		List<NewsArticle> newsArticles = newsService.getLatestNewsArticlesOfTypes();
		if (CollectionUtils.isNotEmpty(newsArticles)) {
			model.addAttribute("newsArticles", newsArticles);
		}
	}

	@GetMapping("/loginerror")
	public String loginError(RedirectAttributes attributes) {
		attributes.addFlashAttribute("loginerror", "Autentimine ebaõnnestus");
		return REDIRECT_PREF + LOGIN_PAGE_URI;
	}

	@PreAuthorize("authentication.principal.datasetPermissionsExist")
	@PostMapping(SELECT_ROLE_URI)
	public String changeRole(@RequestParam Long permissionId) {

		logger.debug("User initiated role change, dataSetPermissionId: {}", permissionId);

		Long userId = userContext.getUserId();
		userProfileService.setRecentDatasetPermission(permissionId, userId);
		userService.updateUserSecurityContext();

		return REDIRECT_PREF + HOME_URI;
	}

	@GetMapping(TERMS_OF_USE_PAGE_URI)
	public String termsOfUse(Model model) {

		String activeTerms = userService.getActiveTermsValue();
		model.addAttribute("activeTerms", activeTerms);
		return TERMS_OF_USE_PAGE;
	}

	@GetMapping(TERMS_AGREEMENT_PAGE_URI)
	public String termsAgreement(Model model) {

		EkiUser user = userContext.getUser();
		boolean activeTermsAgreed = user.isActiveTermsAgreed();
		if (activeTermsAgreed) {
			return REDIRECT_PREF + HOME_URI;
		}

		String activeTerms = userService.getActiveTermsValue();
		model.addAttribute("activeTerms", activeTerms);
		return TERMS_AGREEMENT_PAGE;
	}

	@PostMapping(AGREE_TERMS_URI)
	public String agreeTerms() {

		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		userService.agreeActiveTerms(userId);
		return REDIRECT_PREF + HOME_URI;
	}

	@PostMapping(REFUSE_TERMS_URI)
	public String refuseTerms() {

		EkiUser user = userContext.getUser();
		userService.refuseTerms(user);
		return REDIRECT_PREF + LOGOUT_URI;
	}

}
