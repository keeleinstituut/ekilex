package eki.ekilex.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.UserLoginBean;

@Controller
public class LoginPageController implements WebConstant {

	private static Logger logger = LoggerFactory.getLogger(LoginPageController.class);

	@RequestMapping(value = "/")
	public String login(UserLoginBean userLoginBean, Authentication authentication, Model model, HttpServletRequest request) {

		if (authentication == null) {
			return LOGIN_PAGE;
		}
		if (authentication.isAuthenticated()) {
			return "redirect:" + MAIN_URI;
		}
		return LOGIN_PAGE;
	}

	@RequestMapping(value = LOGOUT_URI)
	public String logout(HttpServletRequest request, HttpServletResponse response) {

		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		if (authentication != null) {
			logger.debug("Logout event for \"{}\"", authentication.getName());
			securityContext.setAuthentication(null);
			SecurityContextHolder.clearContext();
		}
		HttpSession session = request.getSession();
		if (session != null) {
			session.invalidate();
		}

		return "redirect:/";
	}

	@RequestMapping(value = "/error")
	public String loginError(UserLoginBean userLoginBean, Authentication authentication, Model model, HttpServletRequest request) throws Exception {

		model.addAttribute("loginFailed", Boolean.TRUE);

		return LOGIN_PAGE;
	}
}
