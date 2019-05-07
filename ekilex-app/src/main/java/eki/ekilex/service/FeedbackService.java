package eki.ekilex.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import eki.ekilex.data.FeedbackComment;
import org.apache.commons.lang3.StringUtils;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.ekilex.data.Feedback;
import eki.ekilex.service.db.FeedbackDbService;

@Service
public class FeedbackService {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

	private static final String FEEDBACK_TYPE_NEW_WORD = "new_word";
	private static final String FEEDBACK_TYPE_COMMENT = "comment";

	@Autowired
	private FeedbackDbService feedbackDbService;

	@Autowired
	private UserService userService;

	@Transactional
	public List<Feedback> findFeedbackLog() {
		List<Feedback> feedbacks = feedbackDbService.findFeedback();
		List<FeedbackComment> feedbackComments = feedbackDbService.findAllFeedbackComments();
		feedbacks.forEach(fb ->
				fb.setFeedbackComments(feedbackComments.stream()
						.filter(fc -> fc.getFeedbackId().equals(fb.getId()))
						.collect(Collectors.toList())
				)
		);
		return feedbacks;
	}

	@Transactional
	public List<FeedbackComment> getFeedbackComments(Long feedbackId) {
		return feedbackDbService.getFeedbackComments(feedbackId);
	}

	@Transactional
	public void addFeedbackComment(Long feedbackId, String comment) {
		String userName = userService.getAuthenticatedUser().getName();
		feedbackDbService.addFeedbackComment(feedbackId, comment, userName);
	}

	public boolean isValidFeedback(Feedback newFeedback) {
		return newFeedback != null &&
				isValidFeedbackType(newFeedback.getFeedbackType()) &&
				isNotBlank(newFeedback.getSender()) &&
				isNotBlank(newFeedback.getEmail()) &&
				isNotBlank(newFeedback.getWord());
	}

	private boolean isValidFeedbackType(String feedbackType) {
		return StringUtils.equalsAny(feedbackType, FEEDBACK_TYPE_NEW_WORD, FEEDBACK_TYPE_COMMENT);
	}

	@Transactional
	public String addFeedback(Feedback newFeedback) {
		String retMessage = "ok";
		try {
			feedbackDbService.createFeedback(newFeedback);
		} catch (DataAccessException e) {
			logger.error("Add new feedback", e);
			retMessage = "error";
		}
		return retMessage;
	}

	@Transactional
	public void deleteFeedback(Long id) {
		feedbackDbService.deleteFeedback(id);
	}
}
