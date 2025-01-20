package eki.ekilex.service.util;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

import eki.ekilex.data.EkiUser;

@Component
public class UserValidator {

	private static final int MIN_PASSWORD_LENGTH = 8;

	private static final int MIN_NAME_LENGTH = 4;

	public boolean isActiveUser(EkiUser user) {
		if (user == null) {
			return false;
		}
		return StringUtils.isBlank(user.getActivationKey());
	}

	public boolean isEnabledUser(EkiUser user) {
		if (user == null) {
			return false;
		}
		return Boolean.TRUE.equals(user.getEnabled());
	}

	public boolean isValidPassword(String password, String password2) {
		return (StringUtils.length(password) >= MIN_PASSWORD_LENGTH)
				&& StringUtils.equals(password, password2);
	}

	public boolean isValidName(String name) {
		if (StringUtils.isBlank(name)) {
			return false;
		}
		name = StringUtils.trim(name);
		if (!StringUtils.contains(name, " ")) {
			return false;
		}
		if (StringUtils.contains(name, "  ")) {
			return false;
		}
		name = RegExUtils.replaceAll(name, "\\s", "");
		if (StringUtils.length(name) < MIN_NAME_LENGTH) {
			return false;
		}
		if (StringUtils.isAllUpperCase(name)) {
			return false;
		}
		return true;
	}

	public boolean isValidEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return false;
		}
		email = StringUtils.lowerCase(email);
		EmailValidator emailValidator = EmailValidator.getInstance();
		return emailValidator.isValid(email);
	}
}
