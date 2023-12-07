package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class ProtoSearchResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> words;

	private String logMessage;

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

	public String getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

}
