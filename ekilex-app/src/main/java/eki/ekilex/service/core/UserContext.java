package eki.ekilex.service.core;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;

@Component
public class UserContext {

	public boolean isAuthenticatedUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		boolean isAuthenticated = principal instanceof EkiUser;
		return isAuthenticated;
	}

	public EkiUser getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		EkiUser user;
		if (authentication == null) {
			user = new EkiUser();
			user.setName("ekilex");
		} else {
			Object principal = authentication.getPrincipal();
			if (principal instanceof EkiUser) {
				user = (EkiUser) principal;
			} else {
				user = new EkiUser();
				user.setName(principal.toString());
			}
		}
		return user;
	}

	public Long getUserId() {
		return getUser().getId();
	}

	public String getUserName() {
		return getUser().getName();
	}

	public boolean isUserAdmin() {
		return getUser().isAdmin();
	}

	public DatasetPermission getUserRole() {
		return getUser().getRecentRole();
	}

	public void updateUserSecurityContext(EkiUser ekiUser) {
		Collection<? extends GrantedAuthority> authorities = CollectionUtils.emptyCollection();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(ekiUser, null, authorities);
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authenticationToken);
	}
}
