package eki.ekilex.security;

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

import eki.ekilex.constant.ApiConstant;

public class EkiAppAuthenticationManager implements AuthenticationManager, ApiConstant {

	private static Logger logger = LoggerFactory.getLogger(EkiAppAuthenticationManager.class);

	private Map<String, String> acceptedAppKeyNameMap;

	public EkiAppAuthenticationManager(Map<String, String> acceptedAppKeyNameMap) {
		this.acceptedAppKeyNameMap = acceptedAppKeyNameMap;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		Object principal = authentication.getPrincipal();
		if (principal == null) {
			logger.warn("Api key not provided for authentication");
			throw new BadCredentialsException("Api key not provided");
		}
		String providedAppKey = principal.toString();
		if (StringUtils.equals(providedAppKey, EMPTY_API_KEY)) {
			logger.warn("Api key not provided for authentication");
			throw new BadCredentialsException("Api key not provided");
		}
		String appName = acceptedAppKeyNameMap.get(providedAppKey);
		if (appName == null) {
			logger.warn("No such app key exists: \"{}\"", providedAppKey);
			throw new BadCredentialsException("Bad app key");
		}
		Collection<? extends GrantedAuthority> authorities = CollectionUtils.emptyCollection();
		PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(appName, authorities);
		authenticationToken.setAuthenticated(true);
		return authenticationToken;
	}

}
