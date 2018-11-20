package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class GameResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String sessionId;

	private Integer correctAnswersCount;

	private Integer incorrectAnswersCount;

	private Integer allAnswersCount;

	private Float correctAnswersPercent;

	private Long averageAnswerDelay;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Integer getCorrectAnswersCount() {
		return correctAnswersCount;
	}

	public void setCorrectAnswersCount(Integer correctAnswersCount) {
		this.correctAnswersCount = correctAnswersCount;
	}

	public Integer getIncorrectAnswersCount() {
		return incorrectAnswersCount;
	}

	public void setIncorrectAnswersCount(Integer incorrectAnswersCount) {
		this.incorrectAnswersCount = incorrectAnswersCount;
	}

	public Integer getAllAnswersCount() {
		return allAnswersCount;
	}

	public void setAllAnswersCount(Integer allAnswersCount) {
		this.allAnswersCount = allAnswersCount;
	}

	public Float getCorrectAnswersPercent() {
		return correctAnswersPercent;
	}

	public void setCorrectAnswersPercent(Float correctAnswersPercent) {
		this.correctAnswersPercent = correctAnswersPercent;
	}

	public Long getAverageAnswerDelay() {
		return averageAnswerDelay;
	}

	public void setAverageAnswerDelay(Long averageAnswerDelay) {
		this.averageAnswerDelay = averageAnswerDelay;
	}

}
