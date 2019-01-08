package eki.wordweb.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.context.ShutdownEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@ConditionalOnWebApplication
@Configuration
public class ActuatorSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/actuator", "/actuator/**")
			.authenticated()
			.requestMatchers(EndpointRequest.to(ShutdownEndpoint.class))
			.hasRole("ACTUATOR_ADMIN")
			.and()
			.httpBasic()
			.and()
			.csrf().disable();
	}

}
