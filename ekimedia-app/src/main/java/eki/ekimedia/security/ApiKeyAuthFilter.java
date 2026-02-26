package eki.ekimedia.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import eki.common.constant.GlobalConstant;

public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter implements GlobalConstant {

	private String apiKeyHeaderName;

	public ApiKeyAuthFilter(String apiKeyHeaderName) {
		this.apiKeyHeaderName = apiKeyHeaderName;
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		String apiKey = request.getHeader(apiKeyHeaderName);
		if (StringUtils.isBlank(apiKey)) {
			return EMPTY_API_KEY;
		}
		return apiKey;
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return null;
	}

}
