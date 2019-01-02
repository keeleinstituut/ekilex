package eki.ekilex.service;

import eki.ekilex.data.EkiUser;
import eki.ekilex.service.db.UserDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Random;

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
	public boolean isValidNewUser(String email, String name) {
		EkiUser userByEmail = userDbService.getUserByEmail(email);
		EkiUser userByName = userDbService.getUserByName(name);
		return userByEmail == null && userByName == null;
	}

	@Transactional
	public void addNewUser(String email, String name, String password) {
		userDbService.addUser(email, name, password, new String[] {DEFAULT_USER_ROLE});
		EkiUser user = userDbService.getUserByEmail(email);
		logger.debug("Created new user : {}", user.getDescription());
	}

	public String generatePassword() {
		int randomNumber = new Random().nextInt(9000) + 1000;
		return "vana" + randomNumber + "isa";
	}

}
