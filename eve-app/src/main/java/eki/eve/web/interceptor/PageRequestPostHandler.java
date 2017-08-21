package eki.eve.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.eve.constant.SystemConstant;
import eki.eve.constant.WebConstant;

public class PageRequestPostHandler extends HandlerInterceptorAdapter implements WebConstant, SystemConstant {

	public static final String[] AUTHORISED_PATHS = new String[] {"/view/", "/data/", "/favicon.ico"};

	@Autowired
	private AppDataHolder appDataHolder;

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
		ModelMap modelMap = modelAndView.getModelMap();
		/*
		if (modelMap.isEmpty()) {
			return;
		}
		*/
		if (!modelMap.containsKey(APP_DATA_MODEL_KEY)) {
			AppData appData = appDataHolder.getAppData(request, POM_PATH);
			modelMap.addAttribute(APP_DATA_MODEL_KEY, appData);
		}

		// add model attributes here...
	}
}
