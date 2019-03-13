package eki.ekilex.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserApplication;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.UserService;
import eki.ekilex.web.util.UserContext;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class HomeController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserContext userContext;

	@GetMapping(HOME_URI)
	public String home(Model model) {
		EkiUser user = userContext.getUser();
		if (Boolean.TRUE.equals(user.getEnabled())) {
			return HOME_PAGE;			
		}
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
		if (Boolean.TRUE.equals(user.getEnabled())) {
			return "redirect:" + HOME_URI;			
		}
		populateUserApplicationData(user, model);
		return APPLY_PAGE;
	}

	@PostMapping(APPLY_URI)
	public String apply(
			@RequestParam(value = "selectedDatasets", required = false) List<String> selectedDatasets,
			@RequestParam(value = "applicationComment", required = false) String applicationComment,
			Model model) {

		EkiUser user = userContext.getUser();
		if (Boolean.TRUE.equals(user.getEnabled())) {
			return HOME_PAGE;			
		}
		userService.submitUserApplication(selectedDatasets, applicationComment);
		populateUserApplicationData(user, model);
		return APPLY_PAGE;
	}

	private void populateUserApplicationData(EkiUser user, Model model) {

		List<Dataset> datasets = commonDataService.getDatasets();
		List<EkiUserApplication> userApplications = userService.getUserApplications();
		boolean applicationNotSubmitted = (user.getEnabled() == null) && CollectionUtils.isEmpty(userApplications);
		boolean applicationReviewPending = (user.getEnabled() == null) && CollectionUtils.isNotEmpty(userApplications);
		boolean applicationDenied = Boolean.FALSE.equals(user.getEnabled());

		userApplications = userApplications.stream().filter(application -> CollectionUtils.isNotEmpty(application.getDatasetCodes())).collect(Collectors.toList());

		model.addAttribute("datasets", datasets);
		model.addAttribute("applicationNotSubmitted", applicationNotSubmitted);
		model.addAttribute("applicationReviewPending", applicationReviewPending);
		model.addAttribute("applicationDenied", applicationDenied);
		model.addAttribute("userApplications", userApplications);
	}

	@GetMapping("/loginerror")
	public String loginError(RedirectAttributes attributes) {
		attributes.addFlashAttribute("loginerror", "Autentimine ebaõnnestus");
		return "redirect:" + LOGIN_PAGE_URI;
	}
}
