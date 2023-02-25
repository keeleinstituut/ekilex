package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class TermWord extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String value;

	private String lang;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
