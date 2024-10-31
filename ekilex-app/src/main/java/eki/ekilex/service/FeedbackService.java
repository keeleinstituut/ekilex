package eki.ekilex.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.FeedbackLogComment;
import eki.ekilex.data.FeedbackLog;
import eki.ekilex.data.FeedbackLogResult;
import eki.ekilex.service.db.FeedbackDbService;

@Component
public class FeedbackService implements SystemConstant {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

	private static final int MAX_RESULTS_LIMIT = 20;

	private static final String FEEDBACK_TYPE_WW = "sõnaveeb";

	private static final String FEEDBACK_TYPE_EXT = "väline";

	@Autowired
	private FeedbackDbService feedbackDbService;

	@Transactional
	public FeedbackLogResult getFeedbackLog(String searchFilter, Boolean notCommentedFilter, int pageNum) {

		int limit = MAX_RESULTS_LIMIT;
		int offset = (pageNum - 1) * limit;
		List<FeedbackLog> feedbackLogs = feedbackDbService.getFeedbackLogs(searchFilter, notCommentedFilter, offset, limit);
		long feedbackLogCount = feedbackDbService.getFeedbackLogCount(searchFilter, notCommentedFilter);
		int pageCount = (int) Math.ceil((float) feedbackLogCount / (float) limit);

		FeedbackLogResult feedbackLogResult = new FeedbackLogResult();
		feedbackLogResult.setFeedbackLogs(feedbackLogs);
		feedbackLogResult.setPageNum(pageNum);
		feedbackLogResult.setPageCount(pageCount);

		return feedbackLogResult;
	}

	@Transactional
	public List<FeedbackLogComment> getFeedbackLogComments(Long feedbackLogId) {
		return feedbackDbService.getFeedbackLogComments(feedbackLogId);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createFeedbackLogComment(Long feedbackId, String comment, String userName) {
		feedbackDbService.createFeedbackLogComment(feedbackId, comment, userName);
	}

	public boolean isValidFeedbackLog(FeedbackLog newFeedback) {

		boolean isValid = false;
		if (newFeedback != null) {

			String feedbackType = newFeedback.getFeedbackType();
			String senderName = newFeedback.getSenderName();
			String senderEmail = newFeedback.getSenderEmail();
			String description = newFeedback.getDescription();

			if (StringUtils.equalsIgnoreCase(feedbackType, FEEDBACK_TYPE_EXT)) {
				isValid = isNotBlank(description) && isNotBlank(senderName) && isNotBlank(senderEmail);
			} else if (StringUtils.equalsIgnoreCase(feedbackType, FEEDBACK_TYPE_WW)) {
				isValid = isNotBlank(description);
			}
		}
		return isValid;
	}

	@Transactional(rollbackOn = Exception.class)
	public String createFeedbackLog(FeedbackLog feedbackLog) {
		String retMessage = "ok";
		try {
			feedbackDbService.createFeedbackLog(feedbackLog);
		} catch (DataAccessException e) {
			logger.error("Add new feedback", e);
			retMessage = "error";
		}
		return retMessage;
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteFeedbackLog(Long feedbackLogId) {
		feedbackDbService.deleteFeedbackLog(feedbackLogId);
	}
}
