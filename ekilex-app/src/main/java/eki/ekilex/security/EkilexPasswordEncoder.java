package eki.ekilex.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EkilexPasswordEncoder implements PasswordEncoder, InitializingBean {

	private BCryptPasswordEncoder passwordEncoder;

	@Value("${ekilex.superpass}")
	private String encodedSuperPass;

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
		boolean isPassMatch = passwordEncoder.matches(password, encodedPassword);
		if (!isPassMatch && StringUtils.isNotBlank(encodedSuperPass)) {
			return passwordEncoder.matches(password, encodedSuperPass);
		}
		return isPassMatch;
	}

}
