package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class WordClassifier extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String classifierCode;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getClassifierCode() {
		return classifierCode;
	}

	public void setClassifierCode(String classifierCode) {
		this.classifierCode = classifierCode;
	}
}
