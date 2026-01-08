package eki.ekilex.service;

import java.time.LocalDate;
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
import eki.common.data.ExtendedFeedback;
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
	public AppResponse createFeedbackLog(ExtendedFeedback feedback) {

		ValidationResult validationResult = feedbackValidator.validate(feedback);

		if (!validationResult.isValid()) {
			return new AppResponse(FEEDBACK_ERROR, validationResult.getMessageKey());
		}
		try {
			Long feedbackLogId = feedbackDbService.createFeedbackLog(feedback);
			String senderName = feedback.getSenderName();
			String word = feedback.getWord();
			String definition = feedback.getDefinition();
			String usage = feedback.getUsage();
			createFeedbackLogAttr(feedbackLogId, FEEDBACK_ATTR_WORD, word);
			createFeedbackLogAttr(feedbackLogId, FEEDBACK_ATTR_DEFINITION, definition);
			createFeedbackLogAttr(feedbackLogId, FEEDBACK_ATTR_USAGE, usage);
			createFeedbackLogAttr(feedbackLogId, FEEDBACK_ATTR_SENDER_NAME, senderName);
			return new AppResponse(FEEDBACK_OK);
		} catch (DataAccessException e) {
			logger.error("Add new feedback", e);
			return new AppResponse(FEEDBACK_ERROR, "message.feedback.failing.service");
		}
	}

	private void createFeedbackLogAttr(Long feedbackLogId, String name, String value) {
		if (StringUtils.isNotBlank(value)) {
			feedbackDbService.createFeedbackLogAttr(feedbackLogId, name, value);
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
				wordSuggestion.getUsageValue(),
				wordSuggestion.getAuthorName(),
				wordSuggestion.getAuthorEmail());
		if (isBlank) {
			return;
		}
		Long wordSuggestionId = wordSuggestion.getId();
		Long feedbackLogId = wordSuggestion.getFeedbackLogId();
		if (wordSuggestionId == null) {
			FeedbackLog feedbackLog = feedbackDbService.getFeedbackLog(feedbackLogId);
			wordSuggestion.setCreated(feedbackLog.getCreated());
			handlePublication(wordSuggestion);
			feedbackDbService.createWordSuggestion(wordSuggestion);
		} else {
			WordSuggestion existingWordSuggestion = feedbackDbService.getWordSuggestion(wordSuggestionId);
			wordSuggestion.setPublicationDate(existingWordSuggestion.getPublicationDate());
			handlePublication(wordSuggestion);
			feedbackDbService.updateWordSuggestion(wordSuggestion);
		}
	}

	private void handlePublication(WordSuggestion wordSuggestion) {

		boolean isPublic = wordSuggestion.isPublic();
		LocalDate publicationDate = wordSuggestion.getPublicationDate();
		if (isPublic) {
			if (publicationDate == null) {
				LocalDate now = LocalDate.now();
				int dayNow = now.getDayOfMonth();
				if (dayNow < WORD_SUGGESTION_PUBLICATION_DAY) {
					publicationDate = now.withDayOfMonth(WORD_SUGGESTION_PUBLICATION_DAY);
				} else {
					publicationDate = now.plusMonths(1).withDayOfMonth(WORD_SUGGESTION_PUBLICATION_DAY);
				}
			}
		} else {
			publicationDate = null;
		}
		wordSuggestion.setPublicationDate(publicationDate);
	}
}
