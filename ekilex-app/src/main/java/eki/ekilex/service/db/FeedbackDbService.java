package eki.ekilex.service.db;

import eki.ekilex.data.Feedback;
import eki.ekilex.data.db.tables.records.FeedbackLogCommentRecord;
import eki.ekilex.data.db.tables.records.FeedbackLogRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import static eki.ekilex.data.db.Tables.FEEDBACK_LOG;
import static eki.ekilex.data.db.Tables.FEEDBACK_LOG_COMMENT;

@Service
public class FeedbackDbService {

	private DSLContext create;

	public FeedbackDbService(DSLContext context) {
		create = context;
	}

	public void createFeedback(Feedback feedback) {
		FeedbackLogRecord feedbackLogRecord = create.newRecord(FEEDBACK_LOG);
		feedbackLogRecord.from(feedback);
		feedbackLogRecord.insert();
	}

	public void deleteFeedback(Long id) {
		create.delete(FEEDBACK_LOG)
				.where(FEEDBACK_LOG.ID.eq(id))
				.execute();
	}

	public Result<Record> findFeedback() {
		return create
				.select(FEEDBACK_LOG.fields())
				.from(FEEDBACK_LOG)
				.orderBy(FEEDBACK_LOG.CREATED_ON.desc())
				.fetch();
	}

	public Long addFeedbackComment(Long feedbackId, String comment, String userName) {
		FeedbackLogCommentRecord feedbackComment = create.newRecord(FEEDBACK_LOG_COMMENT);
		feedbackComment.setFeedbackLogId(feedbackId);
		feedbackComment.setComment(comment);
		feedbackComment.setUserName(userName);
		feedbackComment.store();
		return feedbackComment.getId();
	}

	public Result<Record> findAllFeedbackComments() {
		return create
				.select(FEEDBACK_LOG_COMMENT.fields())
				.from(FEEDBACK_LOG_COMMENT)
				.fetch();
	}

	public Result<Record> getFeedbackComments(Long feedbackId) {
		return create
				.select(FEEDBACK_LOG_COMMENT.fields())
				.from(FEEDBACK_LOG_COMMENT)
				.where(FEEDBACK_LOG_COMMENT.FEEDBACK_LOG_ID.eq(feedbackId))
				.fetch();
	}

}
