package eki.wordweb.data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.data.AbstractDataObject;
import eki.common.util.LocalDateTimeDeserialiser;

public class WordSuggestion extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordSuggestionId;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime created;

	private String wordValue;

	private String definitionValue;

	private String usageValue;

	private String authorName;

	public Long getWordSuggestionId() {
		return wordSuggestionId;
	}

	public void setWordSuggestionId(Long wordSuggestionId) {
		this.wordSuggestionId = wordSuggestionId;
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

	public String getUsageValue() {
		return usageValue;
	}

	public void setUsageValue(String usageValue) {
		this.usageValue = usageValue;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

}
