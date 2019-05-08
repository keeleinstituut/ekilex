package eki.ekilex.web.controller;

import static java.lang.Thread.sleep;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.security.EkilexPasswordEncoder;
import eki.ekilex.service.EmailService;
import eki.ekilex.service.UserService;

@ConditionalOnWebApplication
@Controller
public class RegisterController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

	@Value("${ekilex.app.url:}")
	private String ekilexAppUrl;

	@Autowired
	private UserService userService;

	@Autowired
	private EkilexPasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@GetMapping(REGISTER_PAGE_URI)
	public String register() {
		boolean isAuthenticatedUser = userService.isAuthenticatedUser();
		if (isAuthenticatedUser) {
			return "redirect:" + HOME_URI;
		}
		return REGISTER_PAGE;
	}

	@PostMapping(REGISTER_PAGE_URI)
	public String registerNewUser(
			@RequestParam("email") String email,
			@RequestParam("name") String name,
			@RequestParam("salasona") String password,
			@RequestParam("salasona2") String password2,
			@RequestParam(value = "ccode", required = false) String honeyPot,
			Model model,
			RedirectAttributes attributes,
			HttpServletRequest request) {
		if (StringUtils.isNotEmpty(honeyPot)) {
			// bot protection triggered
			logger.warn("Bot protection triggered : url - > {} : honey -> {} : name -> {}", request.getRemoteAddr(), honeyPot, name);
			try {
				sleep(10 * 1000);
			} catch (InterruptedException e) {
			}
			return "redirect:" + LOGIN_PAGE_URI;
		}
		if (!userService.isValidPassword(password, password2)) {
			model.addAttribute("userName", name);
			model.addAttribute("userEmail", email);
			model.addAttribute("error_message", "Parool ei sobi, kas liiga lühike või väljade väärtused on erinevad.");
			return REGISTER_PAGE;
		}
		if (userService.isValidUser(email)) {
			String encodedPassword = passwordEncoder.encode(password);
			String activationKey = userService.generateActivationKey();
			String activationLink = ekilexAppUrl + REGISTER_PAGE_URI + ACTIVATE_PAGE_URI + "/" + activationKey;
			userService.createUser(email, name, encodedPassword, activationKey);
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

	@GetMapping(REGISTER_PAGE_URI + ACTIVATE_PAGE_URI + "/{activationKey}")
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
