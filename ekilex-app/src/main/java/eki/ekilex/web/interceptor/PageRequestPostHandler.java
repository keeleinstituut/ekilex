package eki.ekilex.web.interceptor;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.web.util.PermDataUtil;

@ConditionalOnWebApplication
@Component
public class PageRequestPostHandler extends HandlerInterceptorAdapter implements WebConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(PageRequestPostHandler.class);

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	private PermDataUtil permDataUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		long startTime = System.currentTimeMillis();
		request.setAttribute(REQUEST_START_TIME_KEY, startTime);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		if (request.getAttribute("javax.servlet.error.status_code") != null) {
			return;
		}
		if (modelAndView == null) {
			return;
		}
		if (isTraditionalMicrosoftUser(request, modelAndView)) {
			return;
		}

		ModelMap modelMap = modelAndView.getModelMap();
		if (!modelMap.containsKey(APP_DATA_MODEL_KEY)) {
			AppData appData = appDataHolder.getAppData();
			modelMap.addAttribute(APP_DATA_MODEL_KEY, appData);
		}
		if (!modelMap.containsKey(PERM_DATA_UTIL_KEY)) {
			modelMap.addAttribute(PERM_DATA_UTIL_KEY, permDataUtil);
		}

		logRequestProcessTime(request);
	}

	private boolean isTraditionalMicrosoftUser(HttpServletRequest request, ModelAndView modelAndView) {

		String userAgent = request.getHeader("User-Agent");
		if (StringUtils.contains(userAgent, "Trident")) {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			modelAndView.addObject("year", new Integer(year));
			modelAndView.setViewName("iescareoff");
			return true;
		}
		return false;
	}

	private void logRequestProcessTime(HttpServletRequest request) {

		String servletPath = request.getServletPath();
		if (StringUtils.equals(servletPath, "/")) {
			return;
		}
		Object requestStartTimeObj = request.getAttribute(REQUEST_START_TIME_KEY);
		long startTime = Long.valueOf(requestStartTimeObj.toString());
		long endTime = System.currentTimeMillis();
		long requestTime = endTime - startTime;

		logger.info("Request process time for \"{}\" - {} ms", servletPath, requestTime);
	}
}
