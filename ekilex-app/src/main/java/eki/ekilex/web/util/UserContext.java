package eki.ekilex.web.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eki.ekilex.data.EkiUser;

@Component
public class UserContext {

	public EkiUser getUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EkiUser user;
		if (principal instanceof EkiUser) {
			user = (EkiUser) principal;
		} else {
			user = new EkiUser();
			user.setName(principal.toString());
		}
		return user;
	}
}
