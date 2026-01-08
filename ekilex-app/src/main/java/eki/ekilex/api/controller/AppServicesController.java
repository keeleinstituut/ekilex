package eki.ekilex.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import eki.common.data.AppResponse;
import eki.common.data.ExtendedFeedback;
import eki.ekilex.constant.ApiConstant;
import eki.ekilex.service.FeedbackService;

@ConditionalOnWebApplication
@Controller
public class AppServicesController implements ApiConstant {

	private static final Logger logger = LoggerFactory.getLogger(AppServicesController.class);

	@Autowired
	private FeedbackService feedbackService;

	@PostMapping(APP_SERVICES_URI + FEEDBACK_URI)
	@ResponseBody
	public AppResponse createFeedback(
			@RequestBody ExtendedFeedback feedback,
			Authentication authentication,
			HttpServletRequest request) {

		logger.info("Feedback from \"{}\" received: \"{}\"", authentication.getName(), feedback);
		AppResponse appResponse = feedbackService.createFeedbackLog(feedback);
		return appResponse;
	}
}
