package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FEEDBACK_LOG;
import static eki.ekilex.data.db.Tables.FEEDBACK_LOG_COMMENT;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import eki.ekilex.data.Feedback;
import eki.ekilex.data.FeedbackComment;
import eki.ekilex.data.db.tables.records.FeedbackLogCommentRecord;
import eki.ekilex.data.db.tables.records.FeedbackLogRecord;

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

	public List<Feedback> findFeedback() {
		return create
				.select(FEEDBACK_LOG.fields())
				.from(FEEDBACK_LOG)
				.orderBy(FEEDBACK_LOG.CREATED_ON.desc())
				.fetchInto(Feedback.class);
	}

	public Long addFeedbackComment(Long feedbackId, String comment, String userName) {
		FeedbackLogCommentRecord feedbackComment = create.newRecord(FEEDBACK_LOG_COMMENT);
		feedbackComment.setFeedbackLogId(feedbackId);
		feedbackComment.setComment(comment);
		feedbackComment.setUserName(userName);
		feedbackComment.store();
		return feedbackComment.getId();
	}

	public List<FeedbackComment> findAllFeedbackComments() {
		return create
				.select(FEEDBACK_LOG_COMMENT.fields())
				.from(FEEDBACK_LOG_COMMENT)
				.fetchInto(FeedbackComment.class);
	}

	public List<FeedbackComment> getFeedbackComments(Long feedbackId) {
		return create
				.select(FEEDBACK_LOG_COMMENT.fields())
				.from(FEEDBACK_LOG_COMMENT)
				.where(FEEDBACK_LOG_COMMENT.FEEDBACK_LOG_ID.eq(feedbackId))
				.fetchInto(FeedbackComment.class);
	}

}
