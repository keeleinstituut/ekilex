package eki.ekilex.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import eki.ekilex.service.UserService;

public class EkilexAuthenticationManager implements AuthenticationManager {

	private static Logger logger = LoggerFactory.getLogger(EkilexAuthenticationManager.class);

	private UserService userService;

	private PasswordEncoder passwordEncoder;

	public EkilexAuthenticationManager(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String providedUsername = authentication.getPrincipal().toString();
		String providedPassword = authentication.getCredentials().toString();

		Authentication user;
		try {
			user = userService.getUserByName(providedUsername);
		} catch (Exception e) {
			logger.error("Error at finding the user", e);
			return null;
		}
		if (user == null) {
			logger.info("No such user \"{}\"", providedUsername);
			return null;
		}
		String existingPassword = user.getCredentials().toString();
		boolean isPasswordMatch = passwordEncoder.matches(providedPassword, existingPassword);
		user.setAuthenticated(isPasswordMatch);
		if (isPasswordMatch) {
			logger.info("Successful authentication for user \"{}\"", providedUsername);
			return user;
		}
		logger.info("Incorrect password for user \"{}\"", providedUsername);
		return null;
	}

}
