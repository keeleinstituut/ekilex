package eki.ekilex.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import eki.common.web.interceptor.MutingHttpFirewall;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.security.EkilexAuthenticationManager;

@ConditionalOnWebApplication
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebConstant {

	@Autowired
	private EkilexAuthenticationManager authenticationManager;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers(
						INDEX_URI,
						LOGIN_PAGE_URI,
						LOGIN_ERROR_URI,
						REGISTER_PAGE_URI + "/**",
						VIEW_RESOURCES_URI + "/**",
						REST_SERVICES_URI + "/**",
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
		web.httpFirewall(firewall);
	}

	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return authenticationManager;
	}

}
