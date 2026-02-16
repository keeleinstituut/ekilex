package eki.wwexam.web.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UserAgentUtil {

	public boolean isTraditionalMicrosoftUser(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		return StringUtils.contains(userAgent, "Trident");
	}
}
