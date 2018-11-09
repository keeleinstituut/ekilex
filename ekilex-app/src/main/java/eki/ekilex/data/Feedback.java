package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

import javax.persistence.Column;
import java.sql.Timestamp;

public class Feedback extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "feedback_type")
	private String feedbackType;

	@Column(name = "created_on")
	private Timestamp createdOn;

	@Column(name = "sender_name")
	private String sender;

	@Column(name = "sender_email")
	private String email;

	@Column(name = "word")
	private String word;

	@Column(name = "definition")
	private String definition;

	@Column(name = "comments")
	private String comments;

	@Column(name = "usages")
	private String usages;

	@Column(name = "other_info")
	private String otherInfo;

	@Column(name = "last_search")
	private String lastSearch;

	public String getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getUsages() {
		return usages;
	}

	public void setUsages(String usages) {
		this.usages = usages;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public String getLastSearch() {
		return lastSearch;
	}

	public void setLastSearch(String lastSearch) {
		this.lastSearch = lastSearch;
	}

}
