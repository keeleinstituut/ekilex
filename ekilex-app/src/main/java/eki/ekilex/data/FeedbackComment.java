package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

import javax.persistence.Column;
import java.sql.Timestamp;

public class FeedbackComment extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "id")
	private Long id;

	@Column(name = "feedback_log_id")
	private Long feedbackId;

	@Column(name = "created_on")
	private Timestamp createdOn;

	@Column(name = "comment")
	private String comment;

	@Column(name = "user_name")
	private String userName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(Long feedbackId) {
		this.feedbackId = feedbackId;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
