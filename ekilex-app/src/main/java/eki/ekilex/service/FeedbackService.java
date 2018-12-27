package eki.ekilex.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eki.ekilex.data.Feedback;
import eki.ekilex.service.db.FeedbackDbService;

@Service
public class FeedbackService {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

	private static final String FEEDBACK_TYPE_NEW_WORD = "new_word";
	private static final String FEEDBACK_TYPE_COMMENT = "comment";

	private FeedbackDbService feedbackDbService;

	public FeedbackService(FeedbackDbService feedbackDbService) {
		this.feedbackDbService = feedbackDbService;
	}

	@Transactional
	public List<Feedback> findFeedback() {
		return feedbackDbService.findFeedback().into(Feedback.class);
	}

	public boolean isValidFeedback(Feedback newFeedback) {
		return newFeedback != null &&
				isValidFeedbackType(newFeedback.getFeedbackType()) &&
				isNotBlank(newFeedback.getSender()) &&
				isNotBlank(newFeedback.getEmail()) &&
				isNotBlank(newFeedback.getWord());
	}

	@Transactional
	public String addFeedback(Feedback newFeedback) {
		String retMessage = "ok";
		try {
			feedbackDbService.addNewFeedback(newFeedback);
		} catch (DataAccessException e) {
			logger.error("Add new feedback", e);
			retMessage = "error";
		}
		return retMessage;
	}

	private boolean isValidFeedbackType(String feedbackType) {
		return StringUtils.equalsAny(feedbackType, FEEDBACK_TYPE_NEW_WORD, FEEDBACK_TYPE_COMMENT);
	}

}
