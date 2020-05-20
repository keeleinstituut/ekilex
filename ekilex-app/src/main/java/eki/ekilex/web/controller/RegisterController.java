package eki.ekilex.web.controller;

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

import eki.common.util.CodeGenerator;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.EmailService;
import eki.ekilex.service.UserContext;
import eki.ekilex.service.UserService;

@ConditionalOnWebApplication
@Controller
public class RegisterController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

	private static final String BOT_PROTECTION_CODE = "botProtectionCode";

	@Value("${terms.version}")
	private String termsVer;

	@Autowired
	private UserContext userContext;

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@GetMapping(REGISTER_PAGE_URI)
	public String register(Model model, HttpServletRequest request) {
		boolean isAuthenticatedUser = userContext.isAuthenticatedUser();
		if (isAuthenticatedUser) {
			return "redirect:" + HOME_URI;
		}
		setBotProtectionCode(model, request);
		return REGISTER_PAGE;
	}

	@PostMapping(REGISTER_PAGE_URI + "/{botProtectionCode}")
	public String registerNewUser(
			@PathVariable(value = "botProtectionCode", required = false) String botProtectionCode,
			@RequestParam("email") String email,
			@RequestParam("name") String name,
			@RequestParam("salasona") String password,
			@RequestParam("salasona2") String password2,
			@RequestParam(value = "agreement", required = false) boolean agreement,
			Model model,
			RedirectAttributes attributes,
			HttpServletRequest request) {

		boolean isBotProtectionTriggered = checkBotProtection(request, botProtectionCode, email);
		if (isBotProtectionTriggered) {
			return "redirect:" + LOGIN_PAGE_URI;
		}

		if (!agreement) {
			model.addAttribute("userName", name);
			model.addAttribute("userEmail", email);
			model.addAttribute("error_message", "Registreeruda saab ainult tingimustega nõustudes.");
			return REGISTER_PAGE;
		}

		if (!userService.isValidName(name)) {
			model.addAttribute("userName", name);
			model.addAttribute("userEmail", email);
			model.addAttribute("error_message", "Nimi ei sobi. Kontrolli, et see ei oleks liiga lühike ning koosneks ees- ja perekonnanimest. Nimi ei tohi olla läbiva suurtähega.");
			return REGISTER_PAGE;
		}

		if (!userService.isValidPassword(password, password2)) {
			model.addAttribute("userName", name);
			model.addAttribute("userEmail", email);
			model.addAttribute("error_message", "Parool ei sobi, kas liiga lühike või väljade väärtused on erinevad.");
			return REGISTER_PAGE;
		}

		if (userService.isValidUser(email)) {
			String activationLink = userService.createUser(email, name, password, termsVer);
			if (emailService.isEnabled()) {
				attributes.addFlashAttribute("success_message", "Kasutaja registreeritud, aktiveerimise link on saadetud e-postile: " + email);
			} else {
				attributes.addFlashAttribute("success_message", "Kasutaja registreeritud, aktiveeri: " + activationLink);
			}
			return "redirect:" + LOGIN_PAGE_URI;
		} else {
			model.addAttribute("error_message", "Sellise nime või e-posti aadressiga kasutaja on juba registreeritud.");
			return REGISTER_PAGE;
		}
	}

	@PostMapping(FAKE_REGISTER_AND_PASSWORD_RECOVERY_URI)
	public String fakeSubmit(@RequestParam("email") String email, HttpServletRequest request) {

		String clientAddress = request.getRemoteAddr();
		logger.warn("Fake submit action used. Client IP: {}, email: {}", clientAddress, email);
		sleep(10);
		return "redirect:" + LOGIN_PAGE_URI;
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

	@GetMapping(PASSWORD_RECOVERY_URI)
	public String passwordRecoveryPage(Model model, HttpServletRequest request) {
		setBotProtectionCode(model, request);
		return PASSWORD_RECOVERY_PAGE;
	}

	@PostMapping(PASSWORD_RECOVERY_URI + "/{botProtectionCode}")
	public String recoverPassword(@PathVariable(value = "botProtectionCode", required = false) String botProtectionCode, @RequestParam("email") String email,
			Model model, HttpServletRequest request) {

		boolean isBotProtectionTriggered = checkBotProtection(request, botProtectionCode, email);
		if (isBotProtectionTriggered) {
			return "redirect:" + LOGIN_PAGE_URI;
		}

		if (StringUtils.isNotBlank(email)) {
			Long userId = userService.getUserIdByEmail(email);
			if (userId != null) {
				String passwordRecoveryLink = userService.handleUserPasswordRecovery(userId, email);
				if (emailService.isEnabled()) {
					model.addAttribute("message", "Kui sellise e-postiga kasutaja eksisteerib, siis on salasõna muutmise link on saadetud e-postile: " + email);
				} else {
					model.addAttribute("message", "Salasõna muutmise link:  " + passwordRecoveryLink);
				}
			} else {
				if (emailService.isEnabled()) {
					model.addAttribute("message", "Kui sellise e-postiga kasutaja eksisteerib, siis on salasõna muutmise link on saadetud e-postile: " + email);
				} else {
					model.addAttribute("warning", "Salasõna lähtestamine ebaõnnestus");
				}
			}
			return PASSWORD_RECOVERY_PAGE;
		}

		model.addAttribute("warning", "Salasõna lähtestamine ebaõnnestus");
		return PASSWORD_RECOVERY_PAGE;
	}

	@GetMapping(PASSWORD_SET_PAGE_URI + "/{recoveryKey}")
	public String setPasswordPage(@PathVariable(name = "recoveryKey") String recoveryKey, Model model) {

		String userEmail = userService.getUserEmailByRecoveryKey(recoveryKey);
		if (StringUtils.isBlank(userEmail)) {
			model.addAttribute("warning", "Tundmatu salasõna lähtestamise võti.");
			return PASSWORD_RECOVERY_PAGE;
		}
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("recoveryKey", recoveryKey);
		return PASSWORD_SET_PAGE;
	}

	@PostMapping(PASSWORD_SET_PAGE_URI)
	public String setPassword(@RequestParam("salasona") String password, @RequestParam("salasona2") String password2,
			@RequestParam("recoveryKey") String recoveryKey, Model model, RedirectAttributes attributes) {

		if (!userService.isValidPassword(password, password2)) {
			model.addAttribute("error", "Parool ei sobi, kas liiga lühike või väljade väärtused on erinevad.");
			model.addAttribute("recoveryKey", recoveryKey);
			return PASSWORD_SET_PAGE;
		}

		String userEmail = userService.getUserEmailByRecoveryKey(recoveryKey);
		if (StringUtils.isBlank(userEmail)) {
			model.addAttribute("warning", "Tundmatu salasõna lähtestamise võti.");
			return PASSWORD_RECOVERY_PAGE;
		}
		userService.setUserPassword(userEmail, password);
		attributes.addFlashAttribute("success_message", "Parool vahetatud. Logige sisse uue parooliga.");
		attributes.addFlashAttribute("userEmail", userEmail);
		return "redirect:" + LOGIN_PAGE_URI;
	}

	private void setBotProtectionCode(Model model, HttpServletRequest request) {
		String botProtectionCode = CodeGenerator.generateTimestampCode();
		request.getSession().setAttribute(BOT_PROTECTION_CODE, botProtectionCode);
		model.addAttribute(BOT_PROTECTION_CODE, botProtectionCode);
	}

	private boolean checkBotProtection(HttpServletRequest request, String botProtectionCode, String email) {

		String correctBotProtectionCode = (String) request.getSession().getAttribute(BOT_PROTECTION_CODE);
		if (!StringUtils.equals(botProtectionCode, correctBotProtectionCode)) {
			String clientAddress = request.getRemoteAddr();
			logger.warn("Bot protection code validation failed. Client IP: {}, email: {}", clientAddress, email);
			sleep(10);
			return true;
		}
		return false;
	}

	private void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			logger.warn("InterruptedException");
		}
	}

}
