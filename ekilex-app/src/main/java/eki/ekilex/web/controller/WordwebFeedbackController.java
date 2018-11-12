package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Feedback;
import eki.ekilex.service.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class WordwebFeedbackController {

	private static final Logger logger = LoggerFactory.getLogger(WordwebFeedbackController.class);

	private FeedbackService feedbackService;

	public WordwebFeedbackController(FeedbackService service) {
		this.feedbackService = service;
	}

	@GetMapping("/ww_feedback")
	public String openPage(Model model) {
		model.addAttribute("feedbackLog", feedbackService.findFeedback());
		return "ww_feedback";
	}

	@CrossOrigin
	@PostMapping("/send_feedback")
	@ResponseBody
	public String receiveFeedback(Feedback newFeedback) {

		logger.debug("This is feedback : \"{}\"", newFeedback);

		String statusMessage = "error";
		if (feedbackService.isValidFeedback(newFeedback)) {
			statusMessage = feedbackService.addFeedback(newFeedback);
		}
		return "{\"status\": \"" + statusMessage + "\"}";
	}

}
