package eki.ekilex.security;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import eki.ekilex.constant.ApiConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.UserService;
import eki.ekilex.service.util.UserValidator;

public class EkiApiAuthenticationManager implements AuthenticationManager, ApiConstant {

	private static Logger logger = LoggerFactory.getLogger(EkiApiAuthenticationManager.class);

	private UserValidator userValidator;

	private UserService userService;

	public EkiApiAuthenticationManager(UserValidator userValidator, UserService userService) {
		this.userValidator = userValidator;
		this.userService = userService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		Object principal = authentication.getPrincipal();
		if (principal == null) {
			logger.warn("Api key not provided for authentication");
			throw new BadCredentialsException("Api key not provided");
		}
		String providedApiKey = principal.toString();
		if (StringUtils.equals(providedApiKey, EMPTY_API_KEY)) {
			logger.warn("Api key not provided for authentication");
			throw new BadCredentialsException("Api key not provided");
		}
		EkiUser user = userService.getUserByApiKey(providedApiKey);
		if (user == null) {
			logger.warn("No such api key exists: \"{}\"", providedApiKey);
			throw new BadCredentialsException("Bad api key");
		}
		if (!userValidator.isActiveUser(user)) {
			logger.warn("User not activated: \"{}\"", providedApiKey);
			throw new BadCredentialsException("User not activated");
		}
		if (!userValidator.isEnabledUser(user)) {
			logger.warn("User not enabled: \"{}\"", providedApiKey);
			throw new BadCredentialsException("User not enabled");
		}
		Collection<? extends GrantedAuthority> authorities = CollectionUtils.emptyCollection();
		PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(user, authorities);
		authenticationToken.setAuthenticated(true);
		return authenticationToken;
	}

}
