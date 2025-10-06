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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.common.constant.AuthorityOperation;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserApplication;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.service.UserService;
import eki.ekilex.service.util.UserValidator;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class UserProfileController extends AbstractPrivatePageController {

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private UserService userService;

	@GetMapping(USER_PROFILE_URI)
	public String userProfile(Model model) {

		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		List<EkiUserApplication> userApplications = userService.getUserApplications(userId);
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);

		model.addAttribute("user", user);
		model.addAttribute("userProfile", userProfile);
		model.addAttribute("userApplications", userApplications);

		return USER_PROFILE_PAGE;
	}

	@PostMapping(UPDATE_EMAIL_URI)
	public String updateUser(
			@RequestParam("email") String providedEmail,
			RedirectAttributes redirectAttributes) {

		EkiUser user = userContext.getUser();
		String currentEmail = user.getEmail();
		providedEmail = StringUtils.lowerCase(providedEmail);

		if (StringUtils.equals(providedEmail, currentEmail)) {
			// nothing
		} else if (userService.userExists(providedEmail)) {
			addRedirectWarningMessage(redirectAttributes, "userprofile.update.email.warning.duplicate");
		} else if (!userValidator.isValidEmail(providedEmail)) {
			addRedirectWarningMessage(redirectAttributes, "userprofile.update.email.warning.format");
		} else {
			userService.updateUserEmail(providedEmail);
			addRedirectSuccessMessage(redirectAttributes, "userprofile.update.email.success");
		}

		return REDIRECT_PREF + USER_PROFILE_URI;
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

	@PostMapping(LEXEME_COLLOC_EXPAND_URI)
	@ResponseBody
	public String toggleLexemeCollocationExpand(Model model) {

		SessionBean sessionBean = getSessionBean(model);
		sessionBean.setLexemeCollocExpanded(!sessionBean.isLexemeCollocExpanded());

		return RESPONSE_OK_VER2;
	}

	@PostMapping(MEANING_FREEFORM_EXPAND_URI)
	@ResponseBody
	public String toggleMeaningFreeformExpand(Model model) {

		SessionBean sessionBean = getSessionBean(model);
		sessionBean.setMeaningFreeformExpanded(!sessionBean.isMeaningFreeformExpanded());

		return RESPONSE_OK_VER2;
	}

	@PostMapping(MEANING_IMAGE_EXPAND_URI)
	@ResponseBody
	public String toggleMeaningImageExpand(Model model) {

		SessionBean sessionBean = getSessionBean(model);
		sessionBean.setMeaningImageExpanded(!sessionBean.isMeaningImageExpanded());

		return RESPONSE_OK_VER2;
	}

	@PostMapping(MEANING_MEDIA_EXPAND_URI)
	@ResponseBody
	public String toggleMeaningMediaExpand(Model model) {

		SessionBean sessionBean = getSessionBean(model);
		sessionBean.setMeaningMediaExpanded(!sessionBean.isMeaningMediaExpanded());

		return RESPONSE_OK_VER2;
	}

	@PostMapping(GENERATE_API_KEY)
	public String generateApiKey() {

		EkiUser user = userContext.getUser();
		userService.generateApiKey(user);

		return REDIRECT_PREF + USER_PROFILE_URI;
	}
}
