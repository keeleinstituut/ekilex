package eki.ekilex.web.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.ekilex.constant.WebConstant;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class IndexController implements WebConstant {

	@GetMapping("/")
	public String index() {
		return MAIN_PAGE;
	}

	@GetMapping(LOGIN_PAGE_URI)
	public String login() {
		return LOGIN_PAGE;
	}

	@GetMapping("/loginerror")
	public String loginError(RedirectAttributes attributes) {
		attributes.addFlashAttribute("loginerror", "Autentimine ebaõnnestus");
		return "redirect:" + LOGIN_PAGE_URI;
	}
}
