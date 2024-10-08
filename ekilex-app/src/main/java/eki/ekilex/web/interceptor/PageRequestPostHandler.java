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
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.EkiUserRoleData;
import eki.ekilex.web.util.UserProfileUtil;

@ConditionalOnWebApplication
@Component
public class PageRequestPostHandler implements HandlerInterceptor, WebConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(PageRequestPostHandler.class);

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	private UserProfileUtil userProfileUtil;

	@Autowired
	private EkilexInfoContributor ekilexInfoContributor;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		long startTime = System.currentTimeMillis();
		request.setAttribute(REQUEST_START_TIME_KEY, startTime);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		if (modelAndView != null) {
			if (isTraditionalMicrosoftUser(request, modelAndView)) {
				return;
			}
			ModelMap modelMap = modelAndView.getModelMap();
			if (!modelMap.containsKey(APP_DATA_MODEL_KEY)) {
				AppData appData = appDataHolder.getAppData();
				modelMap.addAttribute(APP_DATA_MODEL_KEY, appData);
			}
			if (!modelMap.containsKey(USER_PROFILE_KEY)) {
				EkiUserProfile userProfile = userProfileUtil.getUserProfile();
				modelMap.addAttribute(USER_PROFILE_KEY, userProfile);
			}
			if (!modelMap.containsKey(USER_ROLE_DATA_KEY)) {
				EkiUserRoleData roleData = userProfileUtil.getUserRoleData();
				modelMap.addAttribute(USER_ROLE_DATA_KEY, roleData);
			}
		}

		logRequestProcessTime(request);
	}

	private boolean isTraditionalMicrosoftUser(HttpServletRequest request, ModelAndView modelAndView) {

		String userAgent = request.getHeader("User-Agent");
		if (StringUtils.contains(userAgent, "Trident")) {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			modelAndView.addObject("year", String.valueOf(year));
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
		if (StringUtils.startsWith(servletPath, VIEW_RESOURCES_URI)) {
			return;
		}
		Object requestStartTimeObj = request.getAttribute(REQUEST_START_TIME_KEY);
		long startTime = Long.valueOf(requestStartTimeObj.toString());
		long endTime = System.currentTimeMillis();
		long requestTime = endTime - startTime;

		ekilexInfoContributor.appendRequestTime(requestTime);

		logger.info("Request process time for \"{}\" - {} ms", servletPath, requestTime);
	}
}
