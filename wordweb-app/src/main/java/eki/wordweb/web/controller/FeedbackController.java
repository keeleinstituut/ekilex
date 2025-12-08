package eki.wordweb.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import eki.common.data.AppResponse;
import eki.common.data.Feedback;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.service.FeedbackService;

@ConditionalOnWebApplication
@Controller
public class FeedbackController implements WebConstant {

	@Autowired
	private FeedbackService feedbackService;

	@PostMapping(FEEDBACK_URI)
	@ResponseBody
	public AppResponse feedback(Feedback feedback) {
		return feedbackService.feedback(feedback);
	}
}
