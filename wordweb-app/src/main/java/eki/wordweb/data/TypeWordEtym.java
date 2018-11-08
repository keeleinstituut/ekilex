package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class TypeWordEtym extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long etymWordId;

	private String etymWord;

	private String etymWordLang;

	private String[] etymMeaningWords;

	private String etymTypeCode;

	private String[] comments;

	private boolean isQuestionable;

	private boolean isCompound;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getEtymWordId() {
		return etymWordId;
	}

	public void setEtymWordId(Long etymWordId) {
		this.etymWordId = etymWordId;
	}

	public String getEtymWord() {
		return etymWord;
	}

	public void setEtymWord(String etymWord) {
		this.etymWord = etymWord;
	}

	public String getEtymWordLang() {
		return etymWordLang;
	}

	public void setEtymWordLang(String etymWordLang) {
		this.etymWordLang = etymWordLang;
	}

	public String[] getEtymMeaningWords() {
		return etymMeaningWords;
	}

	public void setEtymMeaningWords(String[] etymMeaningWords) {
		this.etymMeaningWords = etymMeaningWords;
	}

	public String getEtymTypeCode() {
		return etymTypeCode;
	}

	public void setEtymTypeCode(String etymTypeCode) {
		this.etymTypeCode = etymTypeCode;
	}

	public String[] getComments() {
		return comments;
	}

	public void setComments(String[] comments) {
		this.comments = comments;
	}

	public boolean isQuestionable() {
		return isQuestionable;
	}

	public void setQuestionable(boolean isQuestionable) {
		this.isQuestionable = isQuestionable;
	}

	public boolean isCompound() {
		return isCompound;
	}

	public void setCompound(boolean isCompound) {
		this.isCompound = isCompound;
	}

}
