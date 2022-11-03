package eki.stat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import eki.stat.security.ApiAuthManager;
import eki.stat.security.ApiKeyAuthFilter;

@ConditionalOnWebApplication
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${ekistat.service.wordweb.api.key}")
	private String wwApiKey;

	@Value("${ekistat.service.ekilex.api.key}")
	private String ekilexApiKey;

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http
				.antMatcher("/api/**")
				.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().addFilter(createApiKeyAuthFilter())
				.authorizeRequests().anyRequest().authenticated();
	}

	private ApiKeyAuthFilter createApiKeyAuthFilter() {
		ApiAuthManager apiAuthManager = new ApiAuthManager(wwApiKey, ekilexApiKey);
		ApiKeyAuthFilter apiKeyAuthFilter = new ApiKeyAuthFilter();
		apiKeyAuthFilter.setAuthenticationManager(apiAuthManager);
		return apiKeyAuthFilter;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManagerBean();
	}
}
