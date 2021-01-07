package eki.ekilex.web.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LangAdviceController extends AbstractPrivatePageController { // TODO change private -> limited

	@GetMapping(LANG_ADVICE_URI)
	public String stat() {

		return LANG_ADVICE_PAGE;
	}

}
