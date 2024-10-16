package eki.ekilex.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import eki.common.data.AppData;
import eki.common.exception.ApiException;
import eki.common.exception.TermsNotAcceptedException;
import eki.common.util.CodeGenerator;
import eki.common.web.AppDataHolder;
import eki.ekilex.constant.ApiConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.service.api.ApiStatService;

@ConditionalOnWebApplication
@ControllerAdvice
public class ExceptionHandlerController implements WebConstant, ApiConstant {

	private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	private ApiStatService apiStatService;

	@ExceptionHandler(Exception.class)
	public ModelAndView genericException(Authentication authentication, HttpServletRequest request, Exception exception) throws Exception {

		String authName = authentication.getName();
		String exceptionMessage = exception.getMessage();
		String servletPath = request.getServletPath();

		if (StringUtils.startsWith(servletPath, API_SERVICES_URI)) {
			logger.warn("API error \"{}\" when requesting \"{}\"", exceptionMessage, servletPath);
			apiStatService.addError(authName, servletPath, exceptionMessage);
			throw new ApiException(exception);
		}

		ModelAndView modelAndView;

		if (exception instanceof HttpSessionRequiredException) {
			modelAndView = new ModelAndView();
			modelAndView.setViewName(REDIRECT_PREF + INDEX_URI);
			return modelAndView;
		}

		if (exception instanceof TermsNotAcceptedException) {
			modelAndView = new ModelAndView();
			modelAndView.setViewName(REDIRECT_PREF + TERMS_AGREEMENT_PAGE_URI);
			return modelAndView;
		}

		if (AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class) != null) {
			throw exception;
		}

		String errorId = "ERR-ID: " + CodeGenerator.generateUniqueId();

		if (exception instanceof AccessDeniedException) {
			logger.error(errorId + " ({})", exceptionMessage);
		} else {
			logger.error(errorId, exception);
		}

		AppData appData = appDataHolder.getAppData();

		modelAndView = new ModelAndView();
		modelAndView.addObject("errorName", exceptionMessage);
		modelAndView.addObject("errorId", errorId);
		modelAndView.addObject("errorDescription", exception.toString());
		modelAndView.addObject(APP_DATA_MODEL_KEY, appData);
		modelAndView.setViewName(ERROR_PAGE);

		return modelAndView;
	}
}
