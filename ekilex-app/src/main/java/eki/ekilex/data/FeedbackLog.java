package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class FeedbackLog extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String feedbackType;

	private Timestamp createdOn;

	private String senderName;

	private String senderEmail;

	private String word;

	private String definition;

	private String comments;

	private String usages;

	private String otherInfo;

	private String lastSearch;

	private List<FeedbackComment> feedbackComments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
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

	public List<FeedbackComment> getFeedbackComments() {
		return feedbackComments;
	}

	public void setFeedbackComments(List<FeedbackComment> feedbackComments) {
		this.feedbackComments = feedbackComments;
	}

}
