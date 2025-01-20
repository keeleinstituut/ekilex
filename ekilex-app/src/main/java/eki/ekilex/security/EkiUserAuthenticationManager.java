package eki.ekilex.security;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import eki.ekilex.data.EkiUser;
import eki.ekilex.service.UserService;
import eki.ekilex.service.util.UserValidator;

public class EkiUserAuthenticationManager implements AuthenticationManager {

	private static Logger logger = LoggerFactory.getLogger(EkiUserAuthenticationManager.class);

	private UserValidator userValidator;

	private UserService userService;

	private EkilexPasswordEncoder passwordEncoder;

	public EkiUserAuthenticationManager(UserValidator userValidator, UserService userService, EkilexPasswordEncoder passwordEncoder) {
		this.userValidator = userValidator;
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String providedEmail = authentication.getPrincipal().toString();
		String providedPassword = authentication.getCredentials().toString();

		EkiUser user = userService.getUserByEmail(providedEmail);
		if (user == null) {
			logger.info("No such user: \"{}\"", providedEmail);
			throw new BadCredentialsException("No such user exists");
		}
		if (!userValidator.isActiveUser(user)) {
			logger.info("User not activated: \"{}\"", providedEmail);
			throw new BadCredentialsException("User not activated");
		}
		String existingPassword = user.getPassword();
		boolean isPasswordMatch = passwordEncoder.matches(providedPassword, existingPassword);
		if (isPasswordMatch) {
			logger.info("Successful authentication for user: \"{}\"", providedEmail);
			Collection<? extends GrantedAuthority> authorities = CollectionUtils.emptyCollection();
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, existingPassword, authorities);
			return authenticationToken;
		}
		logger.info("Incorrect password for user: \"{}\"", providedEmail);
		throw new BadCredentialsException("Incorrect credentials");
	}

}
