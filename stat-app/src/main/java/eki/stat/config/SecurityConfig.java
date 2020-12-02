package eki.stat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@ConditionalOnWebApplication
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {

		// TODO api key auth?
		http.authorizeRequests().antMatchers("/", "/api/**").permitAll();
	}

}
