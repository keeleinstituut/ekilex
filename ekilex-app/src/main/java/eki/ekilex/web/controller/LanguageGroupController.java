package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	public String languageGroups() {
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
}
