package eki.ekilex.web.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
@PreAuthorize("principal.enabled == true && isAuthenticated() && @permEval.isLimitedPageAccessPermitted(authentication)")
public class LangAdviceController extends AbstractSearchController {

	@GetMapping(LIMITED_TERM_SEARCH_URI)
	public String initSearch(Model model) throws Exception {

		return LIMITED_TERM_SEARCH_PAGE;
	}

}
