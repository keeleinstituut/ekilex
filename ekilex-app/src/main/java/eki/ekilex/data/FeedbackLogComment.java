package eki.ekilex.data;

import java.sql.Timestamp;

import eki.common.data.AbstractDataObject;

public class FeedbackLogComment extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long feedbackLogId;

	private Timestamp createdOn;

	private String comment;

	private String userName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFeedbackLogId() {
		return feedbackLogId;
	}

	public void setFeedbackLogId(Long feedbackLogId) {
		this.feedbackLogId = feedbackLogId;
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
