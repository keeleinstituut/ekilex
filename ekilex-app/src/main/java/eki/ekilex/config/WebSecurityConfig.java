package eki.ekilex.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.security.EkilexAuthenticationManager;

@ConditionalOnWebApplication
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebConstant {

	@Autowired
	private EkilexAuthenticationManager authenticationManager;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers(LOGIN_PAGE_URI, LOGIN_ERROR_URI, "/view/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage(LOGIN_PAGE_URI)
				.loginProcessingUrl(LOGIN_URI)
				.usernameParameter("email")
				.defaultSuccessUrl("/")
				.failureUrl(LOGIN_ERROR_URI)
				.and()
				.logout()
				.logoutUrl(LOGOUT_URI)
				.logoutSuccessUrl("/")
				.and()
				.csrf().disable();
	}

	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return authenticationManager;
	}
}
