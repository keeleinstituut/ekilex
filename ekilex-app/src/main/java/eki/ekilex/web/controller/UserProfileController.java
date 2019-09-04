package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserApplication;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class UserProfileController extends AbstractPageController {

	@GetMapping(USER_PROFILE_URI)
	public String userProfile(Model model) {

		Long userId = userService.getAuthenticatedUser().getId();
		List<EkiUserApplication> userApplications = userService.getUserApplications(userId);
		model.addAttribute("userApplications", userApplications);

		return USER_PROFILE_PAGE;
	}

	@PostMapping(REAPPLY_URI)
	public String reapply(
			@RequestParam(value = "selectedDatasets", required = false) List<String> selectedDatasets,
			@RequestParam(value = "applicationComment", required = false) String applicationComment) {

		EkiUser user = userService.getAuthenticatedUser();
		userService.submitAdditionalUserApplication(user, selectedDatasets, applicationComment);
		return "redirect:" + USER_PROFILE_PAGE;
	}
}
