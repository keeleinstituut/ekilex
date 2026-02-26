package eki.ekimedia.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ConditionalOnWebApplication
@RestControllerAdvice
public class ExceptionHandlerController {

	private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> exception(Exception exception, HttpServletRequest request) {
		logger.warn("API service exception: {}", exception.getMessage());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
}
