package eki.ekilex.service;

import java.time.LocalDateTime;
import java.util.List;

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
import eki.common.data.ValidationResult;
import eki.common.service.util.FeedbackValidator;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.FeedbackLog;
import eki.ekilex.data.FeedbackLogResult;
import eki.ekilex.data.WordSuggestion;
import eki.ekilex.service.db.FeedbackDbService;

@Component
public class FeedbackService implements SystemConstant, FeedbackConstant {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

	private static final int MAX_RESULTS_LIMIT = 20;

	@Autowired
	private FeedbackValidator feedbackValidator;

	@Autowired
	private FeedbackDbService feedbackDbService;

	@Transactional
	public FeedbackLogResult getFeedbackLogs(String searchFilter, Boolean notCommentedFilter, int pageNum) {

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
	public FeedbackLog getFeedbackLog(Long feedbackLogId) {
		return feedbackDbService.getFeedbackLog(feedbackLogId);
	}

	@Transactional(rollbackFor = Exception.class)
	public AppResponse createFeedbackLog(Feedback feedback) {

		ValidationResult validationResult = feedbackValidator.validate(feedback);

		if (!validationResult.isValid()) {
			return new AppResponse(FEEDBACK_ERROR, validationResult.getMessageKey());
		}
		String senderName = feedback.getSenderName();
		String word = feedback.getWord();
		try {
			Long feedbackLogId = feedbackDbService.createFeedbackLog(feedback);
			if (StringUtils.isNotBlank(word)) {
				feedbackDbService.createFeedbackLogAttr(feedbackLogId, FEEDBACK_ATTR_WORD, word);
			}
			if (StringUtils.isNotBlank(senderName)) {
				feedbackDbService.createFeedbackLogAttr(feedbackLogId, FEEDBACK_ATTR_SENDER_NAME, senderName);
			}
			return new AppResponse(FEEDBACK_OK);
		} catch (DataAccessException e) {
			logger.error("Add new feedback", e);
			return new AppResponse(FEEDBACK_ERROR, "message.feedback.failing.service");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteFeedbackLog(Long feedbackLogId) {
		feedbackDbService.deleteFeedbackLog(feedbackLogId);
	}

	@Transactional(rollbackFor = Exception.class)
	public void createFeedbackLogComment(Long feedbackLogId, String comment, String userName) {
		feedbackDbService.createFeedbackLogComment(feedbackLogId, comment, userName);
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveWordSuggestion(WordSuggestion wordSuggestion) {

		boolean isBlank = StringUtils.isAnyBlank(
				wordSuggestion.getWordValue(),
				wordSuggestion.getDefinitionValue(),
				wordSuggestion.getAuthorEmail());
		if (isBlank) {
			return;
		}
		Long wordSuggestionId = wordSuggestion.getId();
		boolean isPublic = wordSuggestion.isPublic();
		LocalDateTime publicationTime = null;
		if (isPublic) {
			publicationTime = LocalDateTime.now();
		} else {
			publicationTime = null;
		}
		wordSuggestion.setPublicationTime(publicationTime);
		if (wordSuggestionId == null) {
			feedbackDbService.createWordSuggestion(wordSuggestion);
		} else {
			feedbackDbService.updateWordSuggestion(wordSuggestion);
		}
	}
}
