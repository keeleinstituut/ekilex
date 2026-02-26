package eki.ekimedia.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import eki.common.constant.GlobalConstant;
import eki.ekimedia.constant.ApiConstant;
import eki.ekimedia.security.ApiAuthManager;
import eki.ekimedia.security.ApiKeyAuthFilter;

@ConditionalOnWebApplication
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements InitializingBean, ApiConstant, GlobalConstant {

	@Value("${ekimedia.service.ekilex.api.key}")
	private String ekilexApiKey;

	private Map<String, String> acceptedApiKeyNameMap;

	@Override
	public void afterPropertiesSet() throws Exception {
		acceptedApiKeyNameMap = new HashMap<>();
		acceptedApiKeyNameMap.put(ekilexApiKey, "ekilex");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http
				.antMatcher(API_SERVICES_URI + "/**")
				.csrf(csrf -> csrf.disable())
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilter(createApiKeyAuthFilter())
				.authorizeHttpRequests()
				.anyRequest()
				.authenticated();
	}

	private ApiKeyAuthFilter createApiKeyAuthFilter() {
		ApiAuthManager apiAuthManager = new ApiAuthManager(acceptedApiKeyNameMap);
		ApiKeyAuthFilter apiKeyAuthFilter = new ApiKeyAuthFilter(EKIMEDIA_API_KEY_HEADER_NAME);
		apiKeyAuthFilter.setAuthenticationManager(apiAuthManager);
		return apiKeyAuthFilter;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManagerBean();
	}
}
