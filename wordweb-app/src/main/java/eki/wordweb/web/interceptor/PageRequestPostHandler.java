package eki.wordweb.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.web.util.UserAgentUtil;

@ConditionalOnWebApplication
@Component
public class PageRequestPostHandler implements HandlerInterceptor, WebConstant, SystemConstant {

	private static final Logger logger = LoggerFactory.getLogger(PageRequestPostHandler.class);

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	protected UserAgentUtil userAgentUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		long startTime = System.currentTimeMillis();
		request.setAttribute(REQUEST_START_TIME_KEY, startTime);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

		if (request.getAttribute("javax.servlet.error.status_code") != null) {
			return;
		}
		if (modelAndView == null) {
			return;
		}

		setIeUserFlag(request);

		ModelMap modelMap = modelAndView.getModelMap();
		if (!modelMap.containsKey(APP_DATA_MODEL_KEY)) {
			AppData appData = appDataHolder.getAppData();
			modelMap.addAttribute(APP_DATA_MODEL_KEY, appData);
		}

		logRequestProcessTime(request);
	}

	private void setIeUserFlag(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute(IE_USER_FLAG_KEY) == null) {
			boolean isIeUser = userAgentUtil.isTraditionalMicrosoftUser(request);
			session.setAttribute(IE_USER_FLAG_KEY, isIeUser);
		}
	}

	private void logRequestProcessTime(HttpServletRequest request) {

		String requestMethod = request.getMethod();
		String servletPath = request.getServletPath();
		if (StringUtils.equals(servletPath, "/")) {
			return;
		}
		Object requestStartTimeObj = request.getAttribute(REQUEST_START_TIME_KEY);
		long startTime = Long.valueOf(requestStartTimeObj.toString());
		long endTime = System.currentTimeMillis();
		long requestTime = endTime - startTime;

		logger.info("Request process time for \"{} {}\" - {} ms", requestMethod, servletPath, requestTime);
	}
}
