package eki.ekilex.data;

import java.time.LocalDateTime;

import eki.common.data.AbstractDataObject;

public class FeedbackLogComment extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long feedbackLogId;

	private LocalDateTime createdOn;

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

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
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
