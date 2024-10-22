package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.FEEDBACK_LOG;
import static eki.ekilex.data.db.main.Tables.FEEDBACK_LOG_COMMENT;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.FeedbackComment;
import eki.ekilex.data.FeedbackLog;
import eki.ekilex.data.db.main.tables.records.FeedbackLogCommentRecord;
import eki.ekilex.data.db.main.tables.records.FeedbackLogRecord;

@Component
public class FeedbackDbService {

	@Autowired
	private DSLContext mainDb;

	public void createFeedbackLog(FeedbackLog feedbackLog) {
		FeedbackLogRecord feedbackLogRecord = mainDb.newRecord(FEEDBACK_LOG);
		feedbackLogRecord.from(feedbackLog);
		feedbackLogRecord.insert();
	}

	public void deleteFeedbackLog(Long feedbackLogId) {
		mainDb.delete(FEEDBACK_LOG)
				.where(FEEDBACK_LOG.ID.eq(feedbackLogId))
				.execute();
	}

	public long getFeedbackLogCount(String searchFilter, Boolean notCommentedFilter) {
		Condition where = getFeedbackLogCond(searchFilter, notCommentedFilter);
		return mainDb.selectCount().from(FEEDBACK_LOG).where(where).fetchOneInto(Long.class);
	}

	public List<FeedbackLog> getFeedbackLogs(String searchFilter, Boolean notCommentedFilter, int offset, int limit) {
		Condition where = getFeedbackLogCond(searchFilter, notCommentedFilter);
		return mainDb
				.selectFrom(FEEDBACK_LOG)
				.where(where)
				.orderBy(FEEDBACK_LOG.CREATED_ON.desc())
				.offset(offset)
				.limit(limit)
				.fetchInto(FeedbackLog.class);
	}

	private Condition getFeedbackLogCond(String searchFilter, Boolean notCommentedFilter) {
		Condition where = DSL.noCondition();
		if (StringUtils.isNotBlank(searchFilter)) {
			String searchCrit = "%" + searchFilter + "%";
			where = where.and(DSL.or(
					FEEDBACK_LOG.SENDER_NAME.likeIgnoreCase(searchCrit),
					FEEDBACK_LOG.SENDER_EMAIL.likeIgnoreCase(searchCrit),
					FEEDBACK_LOG.DESCRIPTION.likeIgnoreCase(searchCrit),
					FEEDBACK_LOG.WORD.likeIgnoreCase(searchCrit),
					FEEDBACK_LOG.DEFINITION.likeIgnoreCase(searchCrit),
					FEEDBACK_LOG.DEFINITION_SOURCE.likeIgnoreCase(searchCrit),
					FEEDBACK_LOG.COMMENTS.likeIgnoreCase(searchCrit),
					FEEDBACK_LOG.USAGE.likeIgnoreCase(searchCrit),
					FEEDBACK_LOG.USAGE_SOURCE.likeIgnoreCase(searchCrit),
					FEEDBACK_LOG.COMPANY.likeIgnoreCase(searchCrit),
					FEEDBACK_LOG.LAST_SEARCH.likeIgnoreCase(searchCrit),
					DSL.exists(DSL
							.select(FEEDBACK_LOG_COMMENT.ID)
							.from(FEEDBACK_LOG_COMMENT)
							.where(FEEDBACK_LOG_COMMENT.COMMENT.likeIgnoreCase(searchCrit)
									.and(FEEDBACK_LOG_COMMENT.FEEDBACK_LOG_ID.eq(FEEDBACK_LOG.ID))))));
		}
		if (Boolean.TRUE.equals(notCommentedFilter)) {
			where = where.andNotExists(DSL
					.select(FEEDBACK_LOG_COMMENT.ID)
					.from(FEEDBACK_LOG_COMMENT)
					.where(FEEDBACK_LOG_COMMENT.FEEDBACK_LOG_ID.eq(FEEDBACK_LOG.ID)));
		}
		return where;
	}

	public Long createFeedbackLogComment(Long feedbackLogId, String comment, String userName) {
		FeedbackLogCommentRecord feedbackComment = mainDb.newRecord(FEEDBACK_LOG_COMMENT);
		feedbackComment.setFeedbackLogId(feedbackLogId);
		feedbackComment.setComment(comment);
		feedbackComment.setUserName(userName);
		feedbackComment.store();
		return feedbackComment.getId();
	}

	public List<FeedbackComment> getFeedbackLogComments() {
		return mainDb
				.selectFrom(FEEDBACK_LOG_COMMENT)
				.orderBy(FEEDBACK_LOG_COMMENT.FEEDBACK_LOG_ID, FEEDBACK_LOG_COMMENT.CREATED_ON.desc())
				.fetchInto(FeedbackComment.class);
	}

	public List<FeedbackComment> getFeedbackLogComments(Long feedbackLogId) {
		return mainDb
				.selectFrom(FEEDBACK_LOG_COMMENT)
				.where(FEEDBACK_LOG_COMMENT.FEEDBACK_LOG_ID.eq(feedbackLogId))
				.orderBy(FEEDBACK_LOG_COMMENT.CREATED_ON.desc())
				.fetchInto(FeedbackComment.class);
	}

}
