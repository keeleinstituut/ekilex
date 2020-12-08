package eki.stat.security;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import eki.common.constant.GlobalConstant;

public class ApiAuthManager implements AuthenticationManager, GlobalConstant {

	private static Logger logger = LoggerFactory.getLogger(ApiAuthManager.class);

	private String[] apiKeys;

	public ApiAuthManager(String... apiKeys) {
		this.apiKeys = apiKeys;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Object principal = authentication.getPrincipal();
		if (principal == null) {
			logger.info("Api key not provided for authentication");
			throw new BadCredentialsException("Api key not provided");
		}
		String providedApiKey = principal.toString();
		if (StringUtils.equals(providedApiKey, EMPTY_API_KEY)) {
			logger.info("Api key not provided for authentication");
			throw new BadCredentialsException("Api key not provided");
		}
		if (!ArrayUtils.contains(apiKeys, providedApiKey)) {
			logger.info("No such api key exists: \"{}\"", providedApiKey);
			throw new BadCredentialsException("Bad api key");
		}
		authentication.setAuthenticated(true);
		return authentication;
	}

}
