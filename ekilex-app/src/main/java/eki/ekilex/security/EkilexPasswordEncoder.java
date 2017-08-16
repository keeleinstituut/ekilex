package eki.ekilex.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EkilexPasswordEncoder implements PasswordEncoder, InitializingBean {

	public static final String PASSWORD_ENCODING_SALT = "ekilexekilex";

	private Md5PasswordEncoder passwordEncoder;

	@Override
	public void afterPropertiesSet() throws Exception {
		passwordEncoder = new Md5PasswordEncoder();
	}

	@Override
	public String encode(CharSequence rawPassword) {
		String password = rawPassword.toString();
		String passwordHash = passwordEncoder.encodePassword(password, PASSWORD_ENCODING_SALT);
		return passwordHash;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String password = rawPassword.toString();
		String passwordHash = passwordEncoder.encodePassword(password, PASSWORD_ENCODING_SALT);
		return StringUtils.equals(passwordHash, encodedPassword);
	}

}
