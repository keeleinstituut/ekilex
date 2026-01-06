package eki.ekilex.data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.data.AbstractDataObject;
import eki.common.util.LocalDateTimeDeserialiser;

public class WordSuggestion extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long feedbackLogId;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime created;

	private String wordValue;

	private String definitionValue;

	private String authorName;

	private String authorEmail;

	private boolean isPublic;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime publicationTime;

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

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public String getDefinitionValue() {
		return definitionValue;
	}

	public void setDefinitionValue(String definitionValue) {
		this.definitionValue = definitionValue;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public LocalDateTime getPublicationTime() {
		return publicationTime;
	}

	public void setPublicationTime(LocalDateTime publicationTime) {
		this.publicationTime = publicationTime;
	}

}
