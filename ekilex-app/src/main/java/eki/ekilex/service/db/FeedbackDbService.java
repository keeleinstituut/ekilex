package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FEEDBACK_LOG;
import static eki.ekilex.data.db.Tables.FEEDBACK_LOG_COMMENT;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Feedback;
import eki.ekilex.data.FeedbackComment;
import eki.ekilex.data.db.tables.records.FeedbackLogCommentRecord;
import eki.ekilex.data.db.tables.records.FeedbackLogRecord;

@Component
public class FeedbackDbService {

	@Autowired
	private DSLContext create;

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

	public List<Feedback> getFeedback() {
		return create
				.select(FEEDBACK_LOG.fields())
				.from(FEEDBACK_LOG)
				.orderBy(FEEDBACK_LOG.CREATED_ON.desc())
				.fetchInto(Feedback.class);
	}

	public Long createFeedbackComment(Long feedbackId, String comment, String userName) {
		FeedbackLogCommentRecord feedbackComment = create.newRecord(FEEDBACK_LOG_COMMENT);
		feedbackComment.setFeedbackLogId(feedbackId);
		feedbackComment.setComment(comment);
		feedbackComment.setUserName(userName);
		feedbackComment.store();
		return feedbackComment.getId();
	}

	public List<FeedbackComment> getAllFeedbackComments() {
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
