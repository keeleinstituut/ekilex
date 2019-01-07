package eki.ekilex.service;

import eki.common.util.CodeGenerator;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.db.UserDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class UserService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	private static final String DEFAULT_USER_ROLE = "basic_user";

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
	public boolean isValidNewUser(String email) {
		if (isBlank(email)) {
			return false;
		}
		EkiUser userByEmail = userDbService.getUserByEmail(email);
		return userByEmail == null;
	}

	@Transactional
	public void addNewUser(String email, String name, String password, String activationKey) {
		userDbService.addUser(email, name, password, new String[] {DEFAULT_USER_ROLE}, activationKey);
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
		return user != null && isBlank(user.getActivationKey());
	}

}
