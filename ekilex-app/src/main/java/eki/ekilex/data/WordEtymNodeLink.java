package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WordEtymNodeLink extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordEtymRelId;

	private Long sourceWordId;

	private Long targetWordId;

	private String sourceWordValue;

	private String targetWordValue;

	private String commentPrese;

	private boolean isQuestionable;

	private boolean isCompound;

	public Long getWordEtymRelId() {
		return wordEtymRelId;
	}

	public void setWordEtymRelId(Long wordEtymRelId) {
		this.wordEtymRelId = wordEtymRelId;
	}

	public Long getSourceWordId() {
		return sourceWordId;
	}

	public void setSourceWordId(Long sourceWordId) {
		this.sourceWordId = sourceWordId;
	}

	public Long getTargetWordId() {
		return targetWordId;
	}

	public void setTargetWordId(Long targetWordId) {
		this.targetWordId = targetWordId;
	}

	public String getSourceWordValue() {
		return sourceWordValue;
	}

	public void setSourceWordValue(String sourceWordValue) {
		this.sourceWordValue = sourceWordValue;
	}

	public String getTargetWordValue() {
		return targetWordValue;
	}

	public void setTargetWordValue(String targetWordValue) {
		this.targetWordValue = targetWordValue;
	}

	public String getCommentPrese() {
		return commentPrese;
	}

	public void setCommentPrese(String commentPrese) {
		this.commentPrese = commentPrese;
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
