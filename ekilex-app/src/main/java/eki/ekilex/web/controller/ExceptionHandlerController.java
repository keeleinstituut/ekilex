package eki.ekilex.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import eki.common.util.CodeGenerator;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.api.ApiResponse;

@ConditionalOnWebApplication
@ControllerAdvice
public class ExceptionHandlerController implements WebConstant {

	private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@ResponseBody
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ApiResponse requestMappingException(MethodArgumentTypeMismatchException exception) {

		return new ApiResponse(false, exception.toString());
	}

	@ResponseBody
	@ExceptionHandler(AccessDeniedException.class)
	public ApiResponse accessDeniedException(AccessDeniedException exception) {

		return new ApiResponse(false, exception.toString());
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView exception(HttpServletRequest request, Exception exception) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		if (exception instanceof HttpSessionRequiredException) {
			modelAndView = new ModelAndView();
			modelAndView.setViewName("redirect:/");
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

		modelAndView = new ModelAndView();
		modelAndView.addObject("errorName", exception.getMessage());
		modelAndView.addObject("errorId", errorId);
		modelAndView.addObject("errorDescription", exception.toString());
		modelAndView.setViewName(ERROR_PAGE);
		return modelAndView;
	}
}
