package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.service.UserService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class UserProfileController extends AbstractPageController {

	@Autowired
	private UserService userService;

	@GetMapping(USER_PROFILE_URI)
	public String userProfile(Model model) {

		Long userId = userContext.getUserId();
		List<EkiUserApplication> userApplications = userService.getUserApplications(userId);
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);

		model.addAttribute("userProfile", userProfile);
		model.addAttribute("userApplications", userApplications);

		return USER_PROFILE_PAGE;
	}

	@PostMapping(REAPPLY_URI)
	public String reapply(
			@RequestParam(name = "selectedDatasets", required = false) List<String> selectedDatasets,
			@RequestParam(name = "applicationComment", required = false) String applicationComment) {

		EkiUser user = userContext.getUser();
		userService.submitAdditionalUserApplication(user, selectedDatasets, applicationComment);
		return "redirect:" + USER_PROFILE_URI;
	}

	@PostMapping(UPDATE_MEANING_REL_PREFS_URI)
	public String updateMeaningRelPrefs(
			@RequestParam("meaningRelationWordLanguages") List<String> meaningRelationWordLanguages,
			@RequestParam(name = "showLexMeaningRelationSourceLangWords", required = false) boolean showLexMeaningRelationSourceLangWords,
			@RequestParam(name = "showMeaningRelationFirstWordOnly", required = false) boolean showMeaningRelationFirstWordOnly,
			@RequestParam(name = "showMeaningRelationMeaningId", required = false) boolean showMeaningRelationMeaningId,
			@RequestParam(name = "showMeaningRelationWordDatasets", required = false) boolean showMeaningRelationWordDatasets) {

		Long userId = userContext.getUserId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		userProfile.setPreferredMeaningRelationWordLangs(meaningRelationWordLanguages);
		userProfile.setShowLexMeaningRelationSourceLangWords(showLexMeaningRelationSourceLangWords);
		userProfile.setShowMeaningRelationFirstWordOnly(showMeaningRelationFirstWordOnly);
		userProfile.setShowMeaningRelationMeaningId(showMeaningRelationMeaningId);
		userProfile.setShowMeaningRelationWordDatasets(showMeaningRelationWordDatasets);
		userProfileService.updateUserProfile(userProfile);
		return "redirect:" + USER_PROFILE_URI;
	}

	@PostMapping(UPDATE_TAG_PREFS_URI)
	public String updateTagPrefs(
			@RequestParam(name = "searchableTags", required = false) List<String> searchableTags,
			@RequestParam(name = "activeTag", required = false) String activeTag) {

		Long userId = userContext.getUserId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		userProfile.setSearchableTags(searchableTags);
		userProfile.setActiveTag(activeTag);
		userProfileService.updateUserProfile(userProfile);
		return "redirect:" + USER_PROFILE_URI;
	}
}
