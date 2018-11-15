package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public abstract class AbstractGameRow extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String remoteAddr;

	private String sessionId;

	private boolean correct;

	private long delay;

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
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
