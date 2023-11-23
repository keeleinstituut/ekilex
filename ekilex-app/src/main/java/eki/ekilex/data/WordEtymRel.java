package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WordEtymRel extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordEtymRelId;

	private String comment;

	private boolean questionable;

	private boolean compound;

	private Long relatedWordId;

	private String relatedWord;

	private String relatedWordLang;

	public Long getWordEtymRelId() {
		return wordEtymRelId;
	}

	public void setWordEtymRelId(Long wordEtymRelId) {
		this.wordEtymRelId = wordEtymRelId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isQuestionable() {
		return questionable;
	}

	public void setQuestionable(boolean questionable) {
		this.questionable = questionable;
	}

	public boolean isCompound() {
		return compound;
	}

	public void setCompound(boolean compound) {
		this.compound = compound;
	}

	public Long getRelatedWordId() {
		return relatedWordId;
	}

	public void setRelatedWordId(Long relatedWordId) {
		this.relatedWordId = relatedWordId;
	}

	public String getRelatedWord() {
		return relatedWord;
	}

	public void setRelatedWord(String relatedWord) {
		this.relatedWord = relatedWord;
	}

	public String getRelatedWordLang() {
		return relatedWordLang;
	}

	public void setRelatedWordLang(String relatedWordLang) {
		this.relatedWordLang = relatedWordLang;
	}

}
