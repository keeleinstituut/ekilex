package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WordEtymPOCRel extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordEtymRelId;

	private String comment;

	private boolean isQuestionable;

	private boolean isCompound;

	private Long relatedWordId;

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
		return isQuestionable;
	}

	public void setQuestionable(boolean questionable) {
		isQuestionable = questionable;
	}

	public boolean isCompound() {
		return isCompound;
	}

	public void setCompound(boolean compound) {
		isCompound = compound;
	}

	public Long getRelatedWordId() {
		return relatedWordId;
	}

	public void setRelatedWordId(Long relatedWordId) {
		this.relatedWordId = relatedWordId;
	}
}
