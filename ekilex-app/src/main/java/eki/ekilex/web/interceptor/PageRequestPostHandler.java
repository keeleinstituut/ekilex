package eki.ekilex.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;

@ConditionalOnWebApplication
@Component
public class PageRequestPostHandler extends HandlerInterceptorAdapter implements WebConstant, SystemConstant {

	@Autowired
	private AppDataHolder appDataHolder;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		if (request.getAttribute("javax.servlet.error.status_code") != null) {
			return;
		}
		if (modelAndView == null) {
			return;
		}

		ModelMap modelMap = modelAndView.getModelMap();
		if (!modelMap.containsKey(APP_DATA_MODEL_KEY)) {
			AppData appData = appDataHolder.getAppData(request, POM_PATH);
			modelMap.addAttribute(APP_DATA_MODEL_KEY, appData);
		}

		// add model attributes here...
	}
}
