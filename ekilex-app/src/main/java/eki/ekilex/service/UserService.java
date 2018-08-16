package eki.ekilex.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.EkiUser;
import eki.ekilex.service.db.UserDbService;

@Component
public class UserService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserDbService userDbService;

	@Transactional
	public EkiUser getUserByEmail(String name) throws Exception {
		EkiUser user = userDbService.getUserByEmail(name);
		return user;
	}
}
