package eki.ekilex.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import eki.ekilex.data.EkiUser;

public class PageRequestPostHandler extends HandlerInterceptorAdapter {

	public static final String[] AUTHORISED_PATHS = new String[] {"/view/", "/data/", "/favicon.ico"};

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		String servletPath = request.getServletPath();
		for (String authorisedPath : AUTHORISED_PATHS) {
			if (StringUtils.startsWith(servletPath, authorisedPath)) {
				return;
			}
		}
		if (request.getAttribute("javax.servlet.error.status_code") != null) {
			return;
		}
		if (modelAndView == null) {
			return;
		}
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		if ((authentication != null) && (authentication instanceof EkiUser)) {
			modelAndView.addObject("user", authentication);
		}
	}
}
