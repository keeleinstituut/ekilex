package eki.ekilex.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import eki.common.web.interceptor.MutingHttpFirewall;
import eki.ekilex.constant.ApiConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.security.ApiKeyAuthFilter;
import eki.ekilex.security.EkiApiAuthenticationManager;
import eki.ekilex.security.EkiUserAuthenticationManager;
import eki.ekilex.security.EkilexPasswordEncoder;
import eki.ekilex.service.UserService;

@ConditionalOnWebApplication
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Configuration
	@Order(1)
	public static class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter implements ApiConstant {

		@Autowired
		private UserService userService;

		@Override
		public void configure(HttpSecurity http) throws Exception {

			http
					.antMatcher(API_SERVICES_URI + "/**")
					.csrf().disable()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and().addFilter(createApiKeyAuthFilter())
					.authorizeRequests().anyRequest().authenticated();
		}

		private ApiKeyAuthFilter createApiKeyAuthFilter() {
			EkiApiAuthenticationManager authenticationManager = new EkiApiAuthenticationManager(userService);
			ApiKeyAuthFilter apiKeyAuthFilter = new ApiKeyAuthFilter(API_KEY_HEADER_NAME);
			apiKeyAuthFilter.setAuthenticationManager(authenticationManager);
			return apiKeyAuthFilter;
		}
	}

	@Configuration
	@Order(2)
	public static class HtmlSecurityConfiguration extends WebSecurityConfigurerAdapter implements WebConstant {

		@Autowired
		private UserService userService;

		@Autowired
		private EkilexPasswordEncoder passwordEncoder;

		@Override
		public void configure(HttpSecurity http) throws Exception {

			http
					.authorizeRequests()
					.antMatchers(
							INDEX_URI,
							LOGIN_PAGE_URI,
							LOGIN_ERROR_URI,
							REGISTER_PAGE_URI + "/**",
							TERMS_OF_USE_PAGE_URI,
							VIEW_RESOURCES_URI + "/**",
							SEND_FEEDBACK_URI,
							FAKE_REGISTER_AND_PASSWORD_RECOVERY_URI,
							PASSWORD_RECOVERY_URI + "/**",
							PASSWORD_SET_PAGE_URI + "/**")
					.permitAll()
					.anyRequest().authenticated()
					.and()
					.formLogin()
					.loginPage(LOGIN_PAGE_URI)
					.loginProcessingUrl(LOGIN_URI)
					.usernameParameter("email")
					.defaultSuccessUrl(HOME_URI)
					.failureUrl(LOGIN_ERROR_URI)
					.and()
					.logout()
					.logoutUrl(LOGOUT_URI)
					.logoutSuccessUrl(INDEX_URI)
					.and()
					.csrf().disable();
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			super.configure(web);
			MutingHttpFirewall firewall = new MutingHttpFirewall();
			firewall.setAllowUrlEncodedSlash(true);
			firewall.setAllowSemicolon(true);
			web.httpFirewall(firewall);
		}

		@Override
		public AuthenticationManager authenticationManager() throws Exception {
			return new EkiUserAuthenticationManager(userService, passwordEncoder);
		}
	}

}
