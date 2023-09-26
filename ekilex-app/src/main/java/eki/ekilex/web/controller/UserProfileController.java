package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.AuthorityOperation;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserApplication;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.service.UserService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class UserProfileController extends AbstractPrivatePageController {

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
			@RequestParam("datasetCode") String datasetCode,
			@RequestParam("authOp") AuthorityOperation authOp,
			@RequestParam("language") String lang,
			@RequestParam("applicationComment") String applicationComment) {

		EkiUser user = userContext.getUser();
		userService.submitUserApplication(user, datasetCode, authOp, lang, applicationComment);

		return REDIRECT_PREF + USER_PROFILE_URI;
	}

	@PostMapping(UPDATE_MEANING_REL_PREFS_URI)
	public String updateMeaningRelPrefs(
			@RequestParam("meaningRelationWordLanguages") List<String> meaningRelationWordLanguages,
			@RequestParam(name = "showLexMeaningRelationSourceLangWords", required = false) boolean showLexMeaningRelationSourceLangWords,
			@RequestParam(name = "showMeaningRelationFirstWordOnly", required = false) boolean showMeaningRelationFirstWordOnly,
			@RequestParam(name = "showMeaningRelationMeaningId", required = false) boolean showMeaningRelationMeaningId,
			@RequestParam(name = "showMeaningRelationWordDatasets", required = false) boolean showMeaningRelationWordDatasets,
			@RequestParam("meaningWordLanguages") List<String> meaningWordLanguages,
			@RequestParam("partSynCandidateLanguages") List<String> partSynCandidateLanguages,
			@RequestParam("fullSynCandidateLanguage") String fullSynCandidateLanguage,
			@RequestParam("fullSynCandidateDatasetCode") String fullSynCandidateDatasetCode) {

		Long userId = userContext.getUserId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		userProfile.setPreferredMeaningRelationWordLangs(meaningRelationWordLanguages);
		userProfile.setShowLexMeaningRelationSourceLangWords(showLexMeaningRelationSourceLangWords);
		userProfile.setShowMeaningRelationFirstWordOnly(showMeaningRelationFirstWordOnly);
		userProfile.setShowMeaningRelationMeaningId(showMeaningRelationMeaningId);
		userProfile.setShowMeaningRelationWordDatasets(showMeaningRelationWordDatasets);
		userProfile.setPreferredSynLexMeaningWordLangs(meaningWordLanguages);
		userProfile.setPreferredPartSynCandidateLangs(partSynCandidateLanguages);
		userProfile.setPreferredFullSynCandidateLang(fullSynCandidateLanguage);
		userProfile.setPreferredFullSynCandidateDatasetCode(fullSynCandidateDatasetCode);
		userProfileService.updateUserProfile(userProfile);

		return REDIRECT_PREF + USER_PROFILE_URI;
	}

	@PostMapping(UPDATE_TAG_PREFS_URI)
	public String updateTagPrefs(
			@RequestParam(name = "preferredTagNames", required = false) List<String> preferredTagNames,
			@RequestParam(name = "activeTagName", required = false) String activeTagName) {

		Long userId = userContext.getUserId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		userProfile.setPreferredTagNames(preferredTagNames);
		userProfile.setActiveTagName(activeTagName);
		userProfileService.updateUserProfile(userProfile);

		return REDIRECT_PREF + USER_PROFILE_URI;
	}

	@PostMapping("/enable_approve_meaning")
	public String enableApproveMeaning(@RequestParam(name = "approveMeaningEnabled", required = false) String approveMeaningEnabledStr) {

		boolean approveMeaningEnabled = StringUtils.equals(approveMeaningEnabledStr, "true");
		Long userId = userContext.getUserId();
		userProfileService.updateApproveMeaningEnabled(userId, approveMeaningEnabled);

		return REDIRECT_PREF + USER_PROFILE_URI;
	}

	@PostMapping(GENERATE_API_KEY)
	public String generateApiKey() {

		EkiUser user = userContext.getUser();
		userService.generateApiKey(user);

		return REDIRECT_PREF + USER_PROFILE_URI;
	}
}
