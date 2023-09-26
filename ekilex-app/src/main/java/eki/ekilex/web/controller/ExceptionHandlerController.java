package eki.ekilex.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import eki.common.data.AppData;
import eki.common.exception.ApiException;
import eki.common.exception.TermsNotAcceptedException;
import eki.common.util.CodeGenerator;
import eki.common.web.AppDataHolder;
import eki.ekilex.constant.ApiConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.api.ApiResponse;

@ConditionalOnWebApplication
@ControllerAdvice
public class ExceptionHandlerController implements WebConstant, ApiConstant {

	private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@Autowired
	private AppDataHolder appDataHolder;

	@ExceptionHandler(Exception.class)
	public ModelAndView genericException(HttpServletRequest request, Exception exception) throws Exception {

		if (StringUtils.startsWith(request.getServletPath(), API_SERVICES_URI)) {
			throw new ApiException(exception);
		}

		ModelAndView modelAndView = new ModelAndView();
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
			logger.error(errorId + " ({})", exception.getMessage());
		} else {
			logger.error(errorId, exception);
		}

		AppData appData = appDataHolder.getAppData();

		modelAndView = new ModelAndView();
		modelAndView.addObject("errorName", exception.getMessage());
		modelAndView.addObject("errorId", errorId);
		modelAndView.addObject("errorDescription", exception.toString());
		modelAndView.addObject(APP_DATA_MODEL_KEY, appData);
		modelAndView.setViewName(ERROR_PAGE);
		return modelAndView;
	}

	@ResponseBody
	@ExceptionHandler(ApiException.class)
	public ApiResponse apiException(ApiException exception) {

		return new ApiResponse(false, exception.toString());
	}
}
