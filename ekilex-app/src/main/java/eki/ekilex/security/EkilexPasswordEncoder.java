package eki.ekilex.security;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EkilexPasswordEncoder implements PasswordEncoder, InitializingBean {

	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public void afterPropertiesSet() throws Exception {
		passwordEncoder = new BCryptPasswordEncoder();
	}

	@Override
	public String encode(CharSequence rawPassword) {
		String password = rawPassword.toString();
		String passwordHash = passwordEncoder.encode(password);
		return passwordHash;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String password = rawPassword.toString();
		return passwordEncoder.matches(password, encodedPassword);
	}

}
