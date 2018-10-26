package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class LexicalDecisionGameRow extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long dataId;

	private String suggestedWordValue;

	private boolean word;

	private String remoteAddr;

	private String localAddr;

	private String sessionId;

	private boolean answer;

	private boolean correct;

	private long delay;

	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}

	public String getSuggestedWordValue() {
		return suggestedWordValue;
	}

	public void setSuggestedWordValue(String suggestedWordValue) {
		this.suggestedWordValue = suggestedWordValue;
	}

	public boolean isWord() {
		return word;
	}

	public void setWord(boolean word) {
		this.word = word;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getLocalAddr() {
		return localAddr;
	}

	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public boolean isAnswer() {
		return answer;
	}

	public void setAnswer(boolean answer) {
		this.answer = answer;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

}
