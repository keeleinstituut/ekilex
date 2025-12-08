package eki.ekilex.app.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
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
import eki.ekilex.security.EkiAppAuthenticationManager;
import eki.ekilex.security.EkiUserAuthenticationManager;
import eki.ekilex.security.EkilexPasswordEncoder;
import eki.ekilex.service.UserService;
import eki.ekilex.service.util.UserValidator;

@ConditionalOnWebApplication
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Configuration
	@Order(1)
	public static class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter implements ApiConstant {

		@Autowired
		private UserValidator userValidator;

		@Autowired
		private UserService userService;

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
			EkiApiAuthenticationManager authenticationManager = new EkiApiAuthenticationManager(userValidator, userService);
			ApiKeyAuthFilter apiKeyAuthFilter = new ApiKeyAuthFilter(API_KEY_HEADER_NAME);
			apiKeyAuthFilter.setAuthenticationManager(authenticationManager);
			return apiKeyAuthFilter;
		}
	}

	@Configuration
	@Order(2)
	public static class AppSecurityConfiguration extends WebSecurityConfigurerAdapter implements ApiConstant, InitializingBean {

		@Value("${wordweb.app.key:#{null}}")
		private String wordwebAppKey;

		private Map<String, String> acceptedAppKeyNameMap;

		@Override
		public void afterPropertiesSet() throws Exception {
			acceptedAppKeyNameMap = new HashMap<>();
			acceptedAppKeyNameMap.put(wordwebAppKey, "wordweb");
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {

			http
					.antMatcher(APP_SERVICES_URI + "/**")
					.csrf(csrf -> csrf.disable())
					.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.addFilter(createApiKeyAuthFilter())
					.authorizeHttpRequests()
					.anyRequest()
					.authenticated();
		}

		private ApiKeyAuthFilter createApiKeyAuthFilter() {
			EkiAppAuthenticationManager authenticationManager = new EkiAppAuthenticationManager(acceptedAppKeyNameMap);
			ApiKeyAuthFilter apiKeyAuthFilter = new ApiKeyAuthFilter(APP_KEY_HEADER_NAME);
			apiKeyAuthFilter.setAuthenticationManager(authenticationManager);
			return apiKeyAuthFilter;
		}

	}

	@Configuration
	@Order(3)
	public static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter implements WebConstant {

		@Autowired
		private UserValidator userValidator;

		@Autowired
		private UserService userService;

		@Autowired
		private EkilexPasswordEncoder passwordEncoder;

		@Override
		public void configure(HttpSecurity http) throws Exception {

			http
					.csrf(csrf -> csrf.disable())
					.authorizeHttpRequests(
							authorize -> authorize
									.antMatchers(INDEX_URI,
											LOGIN_URI,
											LOGIN_ERROR_URI,
											REGISTER_URI + "/**",
											TERMS_OF_USE_URI,
											VIEW_RESOURCES_URI + "/**",
											FAKE_REGISTER_AND_PASSWORD_RECOVERY_URI,
											PASSWORD_RECOVERY_URI + "/**",
											PASSWORD_SET_URI + "/**")
									.permitAll()
									.anyRequest()
									.authenticated())
					.formLogin(login -> login
							.loginPage(LOGIN_URI)
							.loginProcessingUrl(DO_LOGIN_URI)
							.usernameParameter("email")
							.defaultSuccessUrl(HOME_URI)
							.failureUrl(LOGIN_ERROR_URI))
					.logout(logout -> logout
							.logoutUrl(DO_LOGOUT_URI)
							.invalidateHttpSession(true)
							.clearAuthentication(true)
							.deleteCookies("JSESSIONID")
							.logoutSuccessUrl(INDEX_URI));
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			super.configure(web);
			MutingHttpFirewall firewall = new MutingHttpFirewall();
			firewall.setAllowUrlEncodedSlash(true);
			firewall.setAllowSemicolon(true);
			web.httpFirewall(firewall);
		}

		@Bean
		@Override
		public AuthenticationManager authenticationManager() throws Exception {
			return new EkiUserAuthenticationManager(userValidator, userService, passwordEncoder);
		}
	}

}
