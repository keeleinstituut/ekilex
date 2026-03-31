package eki.wordweb.data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.util.LocalDateTimeDeserialiser;

public class DatasetStat extends Dataset {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime lastManualEventOn;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime lastEventTime;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime lastAnythingTime;

	private Integer wordCount;

	private List<DatasetWord> createdMeaningWords;

	private List<DatasetWord> updatedMeaningWords;

	public LocalDateTime getLastManualEventOn() {
		return lastManualEventOn;
	}

	public void setLastManualEventOn(LocalDateTime lastManualEventOn) {
		this.lastManualEventOn = lastManualEventOn;
	}

	public LocalDateTime getLastEventTime() {
		return lastEventTime;
	}

	public void setLastEventTime(LocalDateTime lastEventTime) {
		this.lastEventTime = lastEventTime;
	}

	public LocalDateTime getLastAnythingTime() {
		return lastAnythingTime;
	}

	public void setLastAnythingTime(LocalDateTime lastAnythingTime) {
		this.lastAnythingTime = lastAnythingTime;
	}

	public Integer getWordCount() {
		return wordCount;
	}

	public void setWordCount(Integer wordCount) {
		this.wordCount = wordCount;
	}

	public List<DatasetWord> getCreatedMeaningWords() {
		return createdMeaningWords;
	}

	public void setCreatedMeaningWords(List<DatasetWord> createdMeaningWords) {
		this.createdMeaningWords = createdMeaningWords;
	}

	public List<DatasetWord> getUpdatedMeaningWords() {
		return updatedMeaningWords;
	}

	public void setUpdatedMeaningWords(List<DatasetWord> updatedMeaningWords) {
		this.updatedMeaningWords = updatedMeaningWords;
	}

}
