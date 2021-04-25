package eki.ekilex.log;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import eki.ekilex.data.EkiUser;

public class EkiUserLogConverter extends ClassicConverter {

	@Override
	public String convert(ILoggingEvent event) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return "unauth";
		}
		final Object principal = authentication.getPrincipal();
		if (principal.getClass().getName().equals(EkiUser.class.getName())) {
			try {
				Object idObj = MethodUtils.invokeMethod(principal, "getId");
				if (idObj == null) {
					return "anyone";
				}
				return idObj.toString();
			} catch (Exception e) {
				return "error";
			}
		}
		return "anonymous";
	}

}
