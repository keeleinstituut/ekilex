package eki.ekilex.service;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.FeedbackConstant;
import eki.common.data.AppResponse;
import eki.common.data.Feedback;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.FeedbackLog;
import eki.ekilex.data.FeedbackLogComment;
import eki.ekilex.data.FeedbackLogResult;
import eki.ekilex.service.db.FeedbackDbService;

@Component
public class FeedbackService implements SystemConstant, FeedbackConstant {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

	private static final int MAX_RESULTS_LIMIT = 20;

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

	@Transactional(rollbackFor = Exception.class)
	public AppResponse createFeedbackLog(Feedback feedback) {

		if (feedback == null) {
			return new AppResponse(FEEDBACK_ERROR);
		}
		String feedbackType = feedback.getFeedbackType();
		String description = feedback.getDescription();
		if (!ArrayUtils.contains(FEEDBACK_TYPES, feedbackType)) {
			return new AppResponse(FEEDBACK_ERROR);
		}
		if (StringUtils.isBlank(description)) {
			return new AppResponse(FEEDBACK_ERROR);
		}
		try {
			feedbackDbService.createFeedbackLog(feedback);
			return new AppResponse(FEEDBACK_OK);
		} catch (DataAccessException e) {
			logger.error("Add new feedback", e);
			return new AppResponse(FEEDBACK_ERROR);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteFeedbackLog(Long feedbackLogId) {
		feedbackDbService.deleteFeedbackLog(feedbackLogId);
	}
}
