package eki.ekilex.web.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Feedback;
import eki.ekilex.data.FeedbackComment;
import eki.ekilex.service.FeedbackService;
import eki.ekilex.service.UserService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class WordwebFeedbackController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(WordwebFeedbackController.class);

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private UserService userService;

	@GetMapping(WW_FEEDBACK_URI)
	public String openPage(Model model) {
		List<Feedback> feedbackLog = feedbackService.getFeedbackLog();
		model.addAttribute("feedbackLog", feedbackLog);
		return WW_FEEDBACK_PAGE;
	}

	@CrossOrigin
	@PostMapping(SEND_FEEDBACK_URI)
	@ResponseBody
	public String receiveFeedback(@RequestBody Feedback newFeedback) {

		logger.debug("This is feedback : \"{}\"", newFeedback);

		String statusMessage;
		if (feedbackService.isValidFeedback(newFeedback)) {
			statusMessage = feedbackService.createFeedback(newFeedback);
		} else {
			statusMessage = "error";
		}
		return "{\"status\": \"" + statusMessage + "\"}";
	}

	@PostMapping(WW_FEEDBACK_URI + "/addcomment")
	public String addFeedbackComment(
			@RequestBody Map<String, String> requestBody,
			Model model) {

		String userName = userService.getAuthenticatedUser().getName();
		Long feedbackId = Long.valueOf(requestBody.get("feedbackId"));
		String comment = requestBody.get("comment");

		feedbackService.createFeedbackComment(feedbackId, comment, userName);

		populateFeedbackModel(feedbackId, model);

		return WW_FEEDBACK_PAGE + PAGE_FRAGMENT_ELEM + "eki_comments";
	}

	@GetMapping(WW_FEEDBACK_URI + "/deletefeedback")
	public String deleteDatasetPerm(@RequestParam("feedbackId") Long feedbackId, Model model) {

		feedbackService.deleteFeedback(feedbackId);

		return "redirect:" + WW_FEEDBACK_URI;
	}

	private void populateFeedbackModel(Long feedbackId, Model model) {
		List<FeedbackComment> comments = feedbackService.getFeedbackComments(feedbackId);
		Feedback feedback = new Feedback();
		feedback.setId(feedbackId);
		feedback.setFeedbackComments(comments);

		model.addAttribute("fbItem", feedback);
	}

}
