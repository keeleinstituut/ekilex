package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.security.EkilexPasswordEncoder;
import eki.ekilex.service.EmailService;
import eki.ekilex.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Objects;

import static java.lang.Thread.sleep;

@ConditionalOnWebApplication
@Controller
public class RegisterController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

	@Value("${ekilex.app.url:}")
	private String ekilexAppUrl;

	private UserService userService;

	private EkilexPasswordEncoder passwordEncoder;

	private EmailService emailService;

	public RegisterController(UserService userService, EkilexPasswordEncoder passwordEncoder, EmailService emailService) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
	}

	@GetMapping(REGISTER_PAGE_URI)
	public String register() {
		return REGISTER_PAGE;
	}

	@PostMapping(REGISTER_PAGE_URI)
	public String registerNewUser(
			@RequestParam("email") String email,
			@RequestParam("name") String name,
			@RequestParam("password") String password,
			@RequestParam("password2") String password2,
			@RequestParam(value = "email2", required = false) String honeyPot,
			Model model,
			RedirectAttributes attributes,
			HttpServletRequest request) {
		if (StringUtils.isNotEmpty(honeyPot)) {
			// bot protection triggered
			logger.warn("Bot protection triggered : {} : {} : {} : {}", request.getRemoteAddr(), honeyPot, name);
			try {
				sleep(10 * 1000);
			} catch (InterruptedException e) {
			}
			return "redirect:" + LOGIN_PAGE_URI;
		}
		if (password == null || !Objects.equals(password, password2)) {
			model.addAttribute("userName", name);
			model.addAttribute("userEmail", email);
			model.addAttribute("error_message", "Parooli väljade väärtused on erinevad.");
			return REGISTER_PAGE;
		}
		if (userService.isValidNewUser(email, name)) {
			String encodedPassword = passwordEncoder.encode(password);
			String activationKey = userService.generateActivationKey();
			String activationLink = ekilexAppUrl + ACTIVATION_PAGE_URI + "/" + activationKey;
			userService.addNewUser(email, name, encodedPassword, activationKey);
			// FIXME : remove after email service is properly configured
			if (emailService.isEnabled()) {
				attributes.addFlashAttribute("success_message", "Kasutaja registreeritud, aktiveerimise link on saadetud e-postile : " + email);
			} else {
				attributes.addFlashAttribute("success_message", "Kasutaja registreeritud : aktiveeri " + activationLink);
			}
			String content = "Kasutaja aktiveerimiseks mine lingile : " + "<a href='" + activationLink +"'>" + activationLink + "</a>";
			emailService.sendEmail(
					Collections.singletonList(email),
					Collections.emptyList(),
					"Ekilexi kasutaja registreerimine",
					content);
			return "redirect:" + LOGIN_PAGE_URI;
		} else {
			model.addAttribute("error_message", "Sellise nime või e-posti aadressiga kasutaja on juba registreeritud.");
			return REGISTER_PAGE;
		}
	}

	@GetMapping(ACTIVATION_PAGE_URI + "/{activationKey}")
	public String activate(@PathVariable(name = "activationKey") String activationKey, Model model, RedirectAttributes attributes) {
		EkiUser ekiUser = userService.activateUser(activationKey);
		if (ekiUser == null) {
			model.addAttribute("error_message", "Tundmatu aktiveerimise võti");
			return REGISTER_PAGE;
		} else {
			attributes.addFlashAttribute("success_message", "Kasutaja on aktiveeritud, head kasutamist.");
			attributes.addFlashAttribute("userEmail", ekiUser.getEmail());
			return "redirect:" + LOGIN_PAGE_URI;
		}
	}

}
