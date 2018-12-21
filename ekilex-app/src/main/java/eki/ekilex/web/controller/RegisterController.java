package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.security.EkilexPasswordEncoder;
import eki.ekilex.service.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@ConditionalOnWebApplication
@Controller
public class RegisterController implements WebConstant {

	private UserService userService;

	private EkilexPasswordEncoder passwordEncoder;

	public RegisterController(UserService userService, EkilexPasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping(REGISTER_PAGE_URI)
	public String register() {
		return REGISTER_PAGE;
	}

	@PostMapping(REGISTER_PAGE_URI)
	public String registerNewUser(
			@RequestParam("email") String email,
			@RequestParam("name") String name,
			Model model) {
		if (userService.isValidNewUser(email, name)) {
			String password = userService.generatePassword();
			String encodedPassword = passwordEncoder.encode(password);
			userService.addNewUser(email, name, encodedPassword);
			model.addAttribute("success_message", "Kasutaja registreeritud, parool on saadetud e-postile : " + email + " : parool -> " + password);
		} else {
			model.addAttribute("error_message", "Sellise nime või e-posti aadressiga kasutaja on juba registreeritud.");
		}
		return REGISTER_PAGE;
	}

}
