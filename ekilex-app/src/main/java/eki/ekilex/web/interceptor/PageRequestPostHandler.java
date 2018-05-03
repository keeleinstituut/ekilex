package eki.ekilex.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

@ConditionalOnWebApplication
@Component
public class PageRequestPostHandler extends HandlerInterceptorAdapter implements WebConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(PageRequestPostHandler.class);

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	private MarkdownRenderer markdownRenderer;

	@Autowired
	private TermViewUtil termViewUtil;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		if (request.getAttribute("javax.servlet.error.status_code") != null) {
			return;
		}
		if (modelAndView == null) {
			return;
		}

		String servletPath = request.getServletPath();
		logger.debug("Requested path: \"{}\"", servletPath);

		ModelMap modelMap = modelAndView.getModelMap();
		if (!modelMap.containsKey(APP_DATA_MODEL_KEY)) {
			AppData appData = appDataHolder.getAppData(request, POM_PATH);
			modelMap.addAttribute(APP_DATA_MODEL_KEY, appData);
		}
		if (!modelMap.containsKey(MARKDOWN_RENDERER_KEY)) {
			modelMap.addAttribute(MARKDOWN_RENDERER_KEY, markdownRenderer);
		}
		if (!modelMap.containsKey(TERM_VIEW_UTIL_KEY)) {
			modelMap.addAttribute(TERM_VIEW_UTIL_KEY, termViewUtil);
		}

		// add model attributes here...
	}
}
