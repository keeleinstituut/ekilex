package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WordEtymSyn extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private String wordLang;

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

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

}
