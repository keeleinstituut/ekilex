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
		return userDbService.getUserByEmail(email) == null && userDbService.getUserByName(name) == null;
	}

	@Transactional
	public void addNewUser(String email, String name, String password) {
		userDbService.addUser(email, name, password, new String[] {"user"});
		logger.debug("Uus kasutaja : {}", userDbService.getUserByEmail(email).getDescription());
	}

	public String generatePassword() {
		int randomNumber = new Random().nextInt(9000) + 1000;
		return "vana" + randomNumber + "isa";
	}

}
