package eki.ekilex.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.FeedbackLog;
import eki.ekilex.data.FeedbackLogComment;
import eki.ekilex.data.FeedbackLogResult;
import eki.ekilex.service.db.FeedbackDbService;

@Component
public class FeedbackService implements SystemConstant {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

	private static final int MAX_RESULTS_LIMIT = 20;

	private static final String FEEDBACK_TYPE_WW = "sõnaveeb";

	private static final String FEEDBACK_TYPE_OS = "ÕS";

	private static final String FEEDBACK_TYPE_EXT = "väline";

	private static final String[] FEEDBACK_TYPES = {FEEDBACK_TYPE_WW, FEEDBACK_TYPE_OS, FEEDBACK_TYPE_EXT};

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

	@Transactional(rollbackFor = Exception.class)
	public void createFeedbackLogComment(Long feedbackId, String comment, String userName) {
		feedbackDbService.createFeedbackLogComment(feedbackId, comment, userName);
	}

	public boolean isValidFeedbackLog(FeedbackLog newFeedback) {

		if (newFeedback == null) {
			return false;
		}
		String feedbackType = newFeedback.getFeedbackType();
		String description = newFeedback.getDescription();

		boolean isValid = ArrayUtils.contains(FEEDBACK_TYPES, feedbackType) && isNotBlank(description);
		return isValid;
	}

	@Transactional(rollbackFor = Exception.class)
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

	@Transactional(rollbackFor = Exception.class)
	public void deleteFeedbackLog(Long feedbackLogId) {
		feedbackDbService.deleteFeedbackLog(feedbackLogId);
	}
}
