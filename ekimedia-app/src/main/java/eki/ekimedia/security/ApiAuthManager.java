package eki.ekimedia.security;

import java.util.Collection;
import java.util.Map;

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

import eki.common.constant.GlobalConstant;

public class ApiAuthManager implements AuthenticationManager, GlobalConstant {

	private static Logger logger = LoggerFactory.getLogger(ApiAuthManager.class);

	private Map<String, String> acceptedApiKeyNameMap;

	public ApiAuthManager(Map<String, String> acceptedApiKeyNameMap) {
		this.acceptedApiKeyNameMap = acceptedApiKeyNameMap;
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
		String appName = acceptedApiKeyNameMap.get(providedApiKey);
		if (appName == null) {
			logger.info("No such api key exists: \"{}\"", providedApiKey);
			throw new BadCredentialsException("Bad api key");
		}
		Collection<? extends GrantedAuthority> authorities = CollectionUtils.emptyCollection();
		PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(appName, authorities);
		authenticationToken.setAuthenticated(true);
		return authenticationToken;
	}

}
