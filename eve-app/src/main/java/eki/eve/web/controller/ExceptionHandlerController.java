package eki.eve.web.controller;

import eki.eve.constant.WebConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionHandlerController implements WebConstant {

	private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@ExceptionHandler(Exception.class)
	public ModelAndView exception(Exception exception) throws Exception {
		// If the exception is annotated with @ResponseStatus rethrow it and let the framework handle it.
		// AnnotationUtils is a Spring Framework utility class.
		if (AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class) != null)
			throw exception;

		String errorId = "ERR-ID:" + Math.abs(LocalDateTime.now().toString().hashCode());
		logger.error(errorId, exception);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("errorName", errorId);
		modelAndView.addObject("errorDescription", exception.toString());
		modelAndView.setViewName(ERROR_PAGE);
		return modelAndView;
	}
}
