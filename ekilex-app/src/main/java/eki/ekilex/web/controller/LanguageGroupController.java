package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.LanguageGroup;
import eki.ekilex.service.ClassifierService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LanguageGroupController extends AbstractPrivatePageController {

	@Autowired
	private ClassifierService classifierService;

	@PreAuthorize("principal.admin")
	@GetMapping(LANGUAGE_GROUPS_URI)
	public String languageGroups(Model model) {

		populateModel(model);

		return LANGUAGE_GROUPS_PAGE;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(SAVE_LANGUAGE_GROUP_URI)
	@ResponseBody
	public String saveLanguageGroup(LanguageGroup languageGroup) {

		classifierService.saveLanguageGroup(languageGroup);
		//return RESPONSE_FAIL;
		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(ADD_LANGUAGE_TO_GROUP_URI)
	@ResponseBody
	public String addLanguageToGroup(
			@RequestParam("languageGroupId") Long languageGroupId,
			@RequestParam("languageCode") String languageCode) {

		classifierService.createLanguageGroupMember(languageGroupId, languageCode);

		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(DELETE_LANGUAGE_GROUP_URI)
	public String deleteLanguageGroup(
			@RequestParam("languageGroupId") Long languageGroupId,
			Model model) {

		classifierService.deleteLanguageGroup(languageGroupId);

		populateModel(model);

		return REDIRECT_PREF + LANGUAGE_GROUPS_URI;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(DELETE_LANGUAGE_IN_GROUP)
	public String deleteLanguageInGroup(
			@RequestParam("languageGroupId") Long languageGroupId,
			@RequestParam("languageCode") String languageCode,
			Model model) {

		classifierService.deleteLanguageInGroup(languageGroupId, languageCode);

		populateModel(model);

		return REDIRECT_PREF + LANGUAGE_GROUPS_URI;
	}

	private void populateModel(Model model) {
		List<LanguageGroup> languageGroups = classifierService.getLanguageGroups();
		model.addAttribute("languageGroups", languageGroups);
	}
}
