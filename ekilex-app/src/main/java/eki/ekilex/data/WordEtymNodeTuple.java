package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordEtymNodeTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long wordEtymId;

	private Long wordEtymWordId;

	private String wordEtymWord;

	private String wordEtymWordLang;

	private String etymologyTypeCode;

	private String etymologyYear;

	private String comment;

	private String commentPrese;

	private boolean isQuestionable;

	private List<WordEtymRel> wordEtymRelations;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getWordEtymId() {
		return wordEtymId;
	}

	public void setWordEtymId(Long wordEtymId) {
		this.wordEtymId = wordEtymId;
	}

	public Long getWordEtymWordId() {
		return wordEtymWordId;
	}

	public void setWordEtymWordId(Long wordEtymWordId) {
		this.wordEtymWordId = wordEtymWordId;
	}

	public String getWordEtymWord() {
		return wordEtymWord;
	}

	public void setWordEtymWord(String wordEtymWord) {
		this.wordEtymWord = wordEtymWord;
	}

	public String getWordEtymWordLang() {
		return wordEtymWordLang;
	}

	public void setWordEtymWordLang(String wordEtymWordLang) {
		this.wordEtymWordLang = wordEtymWordLang;
	}

	public String getEtymologyTypeCode() {
		return etymologyTypeCode;
	}

	public void setEtymologyTypeCode(String etymologyTypeCode) {
		this.etymologyTypeCode = etymologyTypeCode;
	}

	public String getEtymologyYear() {
		return etymologyYear;
	}

	public void setEtymologyYear(String etymologyYear) {
		this.etymologyYear = etymologyYear;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public List<WordEtymRel> getWordEtymRelations() {
		return wordEtymRelations;
	}

	public void setWordEtymRelations(List<WordEtymRel> wordEtymRelations) {
		this.wordEtymRelations = wordEtymRelations;
	}
}
