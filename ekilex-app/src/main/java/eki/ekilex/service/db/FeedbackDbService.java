package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.FEEDBACK_LOG;
import static eki.ekilex.data.db.main.Tables.FEEDBACK_LOG_ATTR;
import static eki.ekilex.data.db.main.Tables.FEEDBACK_LOG_COMMENT;
import static eki.ekilex.data.db.main.Tables.WORD_SUGGESTION;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.data.Feedback;
import eki.ekilex.data.db.main.tables.FeedbackLog;
import eki.ekilex.data.db.main.tables.FeedbackLogAttr;
import eki.ekilex.data.db.main.tables.FeedbackLogComment;
import eki.ekilex.service.db.util.QueryHelper;

@Component
public class FeedbackDbService {

	@Autowired
	private DSLContext mainDb;

	@Autowired
	private QueryHelper queryHelper;

	public Long createFeedbackLog(Feedback feedback) {

		return mainDb
				.insertInto(
						FEEDBACK_LOG,
						FEEDBACK_LOG.FEEDBACK_TYPE,
						FEEDBACK_LOG.SENDER_EMAIL,
						FEEDBACK_LOG.LAST_SEARCH,
						FEEDBACK_LOG.DESCRIPTION)
				.values(
						feedback.getFeedbackType().name(),
						feedback.getSenderEmail(),
						feedback.getLastSearch(),
						feedback.getDescription())
				.returning(FEEDBACK_LOG.ID)
				.fetchOne()
				.getId();
	}

	public void createFeedbackLogAttr(Long feedbackLogId, String name, String value) {

		mainDb
				.insertInto(
						FEEDBACK_LOG_ATTR,
						FEEDBACK_LOG_ATTR.FEEDBACK_LOG_ID,
						FEEDBACK_LOG_ATTR.NAME,
						FEEDBACK_LOG_ATTR.VALUE)
				.values(
						feedbackLogId,
						name,
						value)
				.execute();
	}

	public void deleteFeedbackLog(Long feedbackLogId) {

		mainDb
				.delete(FEEDBACK_LOG)
				.where(FEEDBACK_LOG.ID.eq(feedbackLogId))
				.execute();
	}

	public eki.ekilex.data.FeedbackLog getFeedbackLog(Long feedbackLogId) {

		FeedbackLog fl = FEEDBACK_LOG.as("fl");
		List<Field<?>> fields = queryHelper.getFeedbackLogFields(fl);

		return mainDb
				.select(fl.fields())
				.select(fields)
				.from(fl)
				.where(fl.ID.eq(feedbackLogId))
				.fetchOptionalInto(eki.ekilex.data.FeedbackLog.class)
				.orElse(null);
	}

	public List<eki.ekilex.data.FeedbackLog> getFeedbackLogs(String searchFilter, Boolean notCommentedFilter, int offset, int limit) {

		FeedbackLog fl = FEEDBACK_LOG.as("fl");
		List<Field<?>> fields = queryHelper.getFeedbackLogFields(fl);
		Condition where = getFeedbackLogCond(fl, searchFilter, notCommentedFilter);

		return mainDb
				.select(fl.fields())
				.select(fields)
				.from(fl)
				.where(where)
				.orderBy(fl.CREATED.desc())
				.offset(offset)
				.limit(limit)
				.fetchInto(eki.ekilex.data.FeedbackLog.class);
	}

	public long getFeedbackLogCount(String searchFilter, Boolean notCommentedFilter) {

		FeedbackLog fl = FEEDBACK_LOG.as("fl");
		Condition where = getFeedbackLogCond(fl, searchFilter, notCommentedFilter);

		return mainDb
				.select(DSL.count(fl.ID))
				.from(fl)
				.where(where)
				.fetchOneInto(Long.class);
	}

	private Condition getFeedbackLogCond(FeedbackLog fl, String searchFilter, Boolean notCommentedFilter) {

		FeedbackLogComment flc = FEEDBACK_LOG_COMMENT.as("flc");
		FeedbackLogAttr fla = FEEDBACK_LOG_ATTR.as("fla");
		Condition where = DSL.noCondition();

		if (StringUtils.isNotBlank(searchFilter)) {

			String searchCrit = "%" + searchFilter + "%";
			where = where.and(DSL.or(
					fl.SENDER_EMAIL.likeIgnoreCase(searchCrit),
					fl.DESCRIPTION.likeIgnoreCase(searchCrit),
					fl.LAST_SEARCH.likeIgnoreCase(searchCrit),
					DSL.exists(DSL
							.select(fla.ID)
							.from(fla)
							.where(
									fla.FEEDBACK_LOG_ID.eq(fl.ID)
											.and(fla.VALUE.likeIgnoreCase(searchCrit)))),
					DSL.exists(DSL
							.select(flc.ID)
							.from(flc)
							.where(flc.FEEDBACK_LOG_ID.eq(fl.ID)
									.and(flc.COMMENT.likeIgnoreCase(searchCrit))))));
		}
		if (Boolean.TRUE.equals(notCommentedFilter)) {

			where = where.andNotExists(DSL
					.select(flc.ID)
					.from(flc)
					.where(flc.FEEDBACK_LOG_ID.eq(fl.ID)));
		}
		return where;
	}

	public void createFeedbackLogComment(Long feedbackLogId, String comment, String userName) {

		mainDb
				.insertInto(
						FEEDBACK_LOG_COMMENT,
						FEEDBACK_LOG_COMMENT.FEEDBACK_LOG_ID,
						FEEDBACK_LOG_COMMENT.COMMENT,
						FEEDBACK_LOG_COMMENT.USER_NAME)
				.values(
						feedbackLogId,
						comment,
						userName)
				.execute();
	}

	public void createWordSuggestion(eki.ekilex.data.WordSuggestion wordSuggestion) {

		mainDb
				.insertInto(
						WORD_SUGGESTION,
						WORD_SUGGESTION.FEEDBACK_LOG_ID,
						WORD_SUGGESTION.WORD_VALUE,
						WORD_SUGGESTION.DEFINITION_VALUE,
						WORD_SUGGESTION.AUTHOR_NAME,
						WORD_SUGGESTION.AUTHOR_EMAIL,
						WORD_SUGGESTION.IS_PUBLIC,
						WORD_SUGGESTION.PUBLICATION_TIME)
				.values(
						wordSuggestion.getFeedbackLogId(),
						wordSuggestion.getWordValue(),
						wordSuggestion.getDefinitionValue(),
						wordSuggestion.getAuthorName(),
						wordSuggestion.getAuthorEmail(),
						wordSuggestion.isPublic(),
						wordSuggestion.getPublicationTime())
				.execute();
	}

	public void updateWordSuggestion(eki.ekilex.data.WordSuggestion wordSuggestion) {

		mainDb
				.update(WORD_SUGGESTION)
				.set(WORD_SUGGESTION.WORD_VALUE, wordSuggestion.getWordValue())
				.set(WORD_SUGGESTION.DEFINITION_VALUE, wordSuggestion.getDefinitionValue())
				.set(WORD_SUGGESTION.AUTHOR_NAME, wordSuggestion.getAuthorName())
				.set(WORD_SUGGESTION.AUTHOR_EMAIL, wordSuggestion.getAuthorEmail())
				.set(WORD_SUGGESTION.IS_PUBLIC, wordSuggestion.isPublic())
				.set(WORD_SUGGESTION.PUBLICATION_TIME, wordSuggestion.getPublicationTime())
				.where(WORD_SUGGESTION.ID.eq(wordSuggestion.getId()))
				.execute();
	}
}
