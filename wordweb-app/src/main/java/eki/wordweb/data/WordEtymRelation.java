package eki.wordweb.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import eki.common.data.AbstractDataObject;

public class WordEtymRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordEtymRelId;

	private String comment;

	@JsonProperty("is_questionable")
	private boolean isQuestionable;

	@JsonProperty("is_compound")
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

	public void setQuestionable(boolean isQuestionable) {
		this.isQuestionable = isQuestionable;
	}

	public boolean isCompound() {
		return isCompound;
	}

	public void setCompound(boolean isCompound) {
		this.isCompound = isCompound;
	}

	public Long getRelatedWordId() {
		return relatedWordId;
	}

	public void setRelatedWordId(Long relatedWordId) {
		this.relatedWordId = relatedWordId;
	}

}
