package eki.ekilex.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.web.util.UserContext;

//TODO under construction!
public class PermissionsController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(PermissionsController.class);

	@Autowired
	private UserContext userContext;

	@GetMapping("/permissions")
	public String permissions() {
		EkiUser user = userContext.getUser();
		if (!user.isAdmin()) {
			return "redirect:" + HOME_URI;
		}
		return "permissions";
	}
}
