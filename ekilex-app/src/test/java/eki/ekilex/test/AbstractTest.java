package eki.ekilex.test;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import eki.ekilex.data.EkiUser;
import eki.ekilex.service.UserService;

public abstract class AbstractTest {

	@Autowired
	private UserService userService;

	public void initSecurity() {

		EkiUser user = userService.getUserByEmail("test@test.com");
		String password = "testtesttest";
		SecurityContext securityContext = SecurityContextHolder.getContext();
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, password, Collections.emptyList());
		securityContext.setAuthentication(authToken);
	}
}
