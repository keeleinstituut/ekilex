package eki.eve.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import eki.eve.constant.WebConstant;

@ControllerAdvice
public class ExceptionHandlerController {

	private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@ExceptionHandler(Exception.class)
	public ModelAndView exception(HttpServletRequest request, Exception exception) {

		ModelAndView modelAndView = new ModelAndView();

		if (exception instanceof HttpSessionRequiredException) {
			modelAndView.setViewName("redirect:/");
			return modelAndView;
		} else {
			logger.debug("Does this kind of exception require handling? {}", exception.getClass());
		}
		modelAndView.addObject("errorName", exception.getMessage());
		modelAndView.addObject("errorDescription", exception.toString());
		modelAndView.setViewName(WebConstant.ERROR_PAGE);

		logger.error("Eve app system exception. Sorry about that!", exception);

		return modelAndView;
	}
}
