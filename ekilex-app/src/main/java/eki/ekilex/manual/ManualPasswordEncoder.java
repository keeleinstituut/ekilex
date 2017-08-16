package eki.ekilex.manual;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import eki.ekilex.security.EkilexPasswordEncoder;

public class ManualPasswordEncoder {

	public static void main(String[] args) {
		try {
			String password = "Malle";

			Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
			String encodedPassword = passwordEncoder.encodePassword(password, EkilexPasswordEncoder.PASSWORD_ENCODING_SALT);

			System.out.println(encodedPassword);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
