package eki.ekilex.service;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.util.CodeGenerator;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.db.UserDbService;

@Component
public class UserService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	private static final int MIN_PASSWORD_LENGTH = 8;

	private UserDbService userDbService;

	public UserService(UserDbService userDbService) {
		this.userDbService = userDbService;
	}

	@Transactional
	public EkiUser getUserByEmail(String email) {
		EkiUser user = userDbService.getUserByEmail(email);
		return user;
	}

	@Transactional
	public boolean isValidUser(String email) {
		if (StringUtils.isBlank(email)) {
			return false;
		}
		EkiUser user = userDbService.getUserByEmail(email);
		return user == null;
	}

	@Transactional
	public void createUser(String email, String name, String password, String activationKey) {
		userDbService.createUser(email, name, password, activationKey);
		EkiUser user = userDbService.getUserByEmail(email);
		logger.debug("Created new user : {}", user.getDescription());
	}

	@Transactional
	public EkiUser activateUser(String activationKey) {
		EkiUser user = userDbService.activateUser(activationKey);
		return user;
	}

	public String generateActivationKey() {
		return CodeGenerator.generateUniqueId();
	}

	public boolean isActiveUser(EkiUser user) {
		return user != null && StringUtils.isBlank(user.getActivationKey());
	}

	public boolean isValidPassword(String password, String password2) {
		return StringUtils.length(password) >= MIN_PASSWORD_LENGTH && StringUtils.equals(password, password2);
	}

}
