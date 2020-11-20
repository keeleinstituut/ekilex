package eki.ekilex.cli;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ManualPasswordEncoder {

	public static void main(String[] args) {
		try {
			String password = "?????";

			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(password);

			System.out.println(encodedPassword);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
