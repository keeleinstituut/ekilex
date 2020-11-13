package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class ParadigmWord extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordClass;

	public ParadigmWord(Long wordId, String wordClass) {
		this.wordId = wordId;
		this.wordClass = wordClass;
	}

	public Long getWordId() {
		return wordId;
	}

	public String getWordClass() {
		return wordClass;
	}

}
