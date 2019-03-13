package eki.ekilex.web.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eki.ekilex.data.EkiUser;

@Component
public class UserContext {

	public EkiUser getUser() {
		EkiUser user = (EkiUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user;
	}
}
