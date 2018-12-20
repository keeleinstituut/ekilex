package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@ConditionalOnWebApplication
@Controller
public class RegisterController implements WebConstant {

	@GetMapping(REGISTER_PAGE_URI)
	public String register() {
		return REGISTER_PAGE;
	}

	@PostMapping(REGISTER_PAGE_URI)
	public String registerNewUser(
			@RequestParam("email") String email,
			@RequestParam("name") String name,
			Model model) {
		model.addAttribute("register_message", "Under construction : " + email + " : " + name);
		return REGISTER_PAGE;
	}

}
