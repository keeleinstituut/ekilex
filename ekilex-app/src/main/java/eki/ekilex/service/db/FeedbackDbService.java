package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FEEDBACK_LOG;
import static eki.ekilex.data.db.Tables.FEEDBACK_LOG_COMMENT;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.FeedbackComment;
import eki.ekilex.data.FeedbackLog;
import eki.ekilex.data.db.tables.records.FeedbackLogCommentRecord;
import eki.ekilex.data.db.tables.records.FeedbackLogRecord;

@Component
public class FeedbackDbService {

	@Autowired
	private DSLContext create;

	public void createFeedbackLog(FeedbackLog feedbackLog) {
		FeedbackLogRecord feedbackLogRecord = create.newRecord(FEEDBACK_LOG);
		feedbackLogRecord.from(feedbackLog);
		feedbackLogRecord.insert();
	}

	public void deleteFeedbackLog(Long feedbackLogId) {
		create.delete(FEEDBACK_LOG)
				.where(FEEDBACK_LOG.ID.eq(feedbackLogId))
				.execute();
	}

	public long getFeedbackLogCount(String senderEmailFilter, Boolean notCommentedFilter) {
		Condition where = getFeedbackLogCond(senderEmailFilter, notCommentedFilter);
		return create.selectCount().from(FEEDBACK_LOG).where(where).fetchOneInto(Long.class);
	}

	public List<FeedbackLog> getFeedbackLogs(String senderEmailFilter, Boolean notCommentedFilter, int offset, int limit) {
		Condition where = getFeedbackLogCond(senderEmailFilter, notCommentedFilter);
		return create
				.selectFrom(FEEDBACK_LOG)
				.where(where)
				.orderBy(FEEDBACK_LOG.CREATED_ON.desc())
				.offset(offset)
				.limit(limit)
				.fetchInto(FeedbackLog.class);
	}

	private Condition getFeedbackLogCond(String senderEmailFilter, Boolean notCommentedFilter) {
		Condition where = DSL.noCondition();
		if (StringUtils.isNotBlank(senderEmailFilter)) {
			String senderEmailCrit = "%" + StringUtils.lowerCase(senderEmailFilter) + "%";
			where = where.and(FEEDBACK_LOG.SENDER_EMAIL.like(DSL.lower(senderEmailCrit)));
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
		FeedbackLogCommentRecord feedbackComment = create.newRecord(FEEDBACK_LOG_COMMENT);
		feedbackComment.setFeedbackLogId(feedbackLogId);
		feedbackComment.setComment(comment);
		feedbackComment.setUserName(userName);
		feedbackComment.store();
		return feedbackComment.getId();
	}

	public List<FeedbackComment> getFeedbackLogComments() {
		return create
				.selectFrom(FEEDBACK_LOG_COMMENT)
				.orderBy(FEEDBACK_LOG_COMMENT.FEEDBACK_LOG_ID, FEEDBACK_LOG_COMMENT.CREATED_ON.desc())
				.fetchInto(FeedbackComment.class);
	}

	public List<FeedbackComment> getFeedbackLogComments(Long feedbackLogId) {
		return create
				.selectFrom(FEEDBACK_LOG_COMMENT)
				.where(FEEDBACK_LOG_COMMENT.FEEDBACK_LOG_ID.eq(feedbackLogId))
				.orderBy(FEEDBACK_LOG_COMMENT.CREATED_ON.desc())
				.fetchInto(FeedbackComment.class);
	}

}
