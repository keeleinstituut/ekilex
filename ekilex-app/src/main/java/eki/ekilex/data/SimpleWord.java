package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class SimpleWord extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private String lang;

	public SimpleWord() {
	}

	public SimpleWord(Long wordId, String wordValue, String lang) {
		this.wordId = wordId;
		this.wordValue = wordValue;
		this.lang = lang;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
