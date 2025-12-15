package eki.wordweb.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import eki.common.data.AppResponse;
import eki.common.data.Feedback;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.service.FeedbackService;

@ConditionalOnWebApplication
@Controller
public class FeedbackController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

	@Autowired
	private FeedbackService feedbackService;

	// dummy response for hackers
	@GetMapping("/feed")
	public String feedGet() {
		logger.warn("Illegal attempt to access feedback form");
		return "nothing";
	}

	// dummy response for hackers
	@PostMapping("/feed")
	public String feedPost() {
		logger.warn("Illegal attempt to access feedback form");
		return "nothing";
	}

	@PostMapping(FEEDBACK_URI)
	@ResponseBody
	public AppResponse feedback(Feedback feedback) {
		return feedbackService.feedback(feedback);
	}
}
