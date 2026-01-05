package eki.ekilex.data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.constant.FeedbackType;
import eki.common.data.AbstractDataObject;
import eki.common.util.LocalDateTimeDeserialiser;

public class FeedbackLog extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime created;

	private FeedbackType feedbackType;

	private String senderEmail;

	private String lastSearch;

	private String description;

	private List<FeedbackLogAttr> feedbackLogAttrs;

	private List<FeedbackLogComment> feedbackLogComments;

	private WordSuggestion wordSuggestion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public FeedbackType getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(FeedbackType feedbackType) {
		this.feedbackType = feedbackType;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getLastSearch() {
		return lastSearch;
	}

	public void setLastSearch(String lastSearch) {
		this.lastSearch = lastSearch;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<FeedbackLogAttr> getFeedbackLogAttrs() {
		return feedbackLogAttrs;
	}

	public void setFeedbackLogAttrs(List<FeedbackLogAttr> feedbackLogAttrs) {
		this.feedbackLogAttrs = feedbackLogAttrs;
	}

	public List<FeedbackLogComment> getFeedbackLogComments() {
		return feedbackLogComments;
	}

	public void setFeedbackLogComments(List<FeedbackLogComment> feedbackLogComments) {
		this.feedbackLogComments = feedbackLogComments;
	}

	public WordSuggestion getWordSuggestion() {
		return wordSuggestion;
	}

	public void setWordSuggestion(WordSuggestion wordSuggestion) {
		this.wordSuggestion = wordSuggestion;
	}

}
