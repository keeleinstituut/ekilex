package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordEtymPOCTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long wordEtymId;

	private Long wordEtymWordId;

	private String wordEtymWord;

	private String wordEtymWordLang;

	private String etymologyTypeCode;

	private String etymologyYear;

	private String wordEtymComment;

	private boolean wordEtymIsQuestionable;

	private List<WordEtymPOCRel> wordEtymRelations;

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

	public String getWordEtymComment() {
		return wordEtymComment;
	}

	public void setWordEtymComment(String wordEtymComment) {
		this.wordEtymComment = wordEtymComment;
	}

	public boolean isWordEtymIsQuestionable() {
		return wordEtymIsQuestionable;
	}

	public void setWordEtymIsQuestionable(boolean wordEtymIsQuestionable) {
		this.wordEtymIsQuestionable = wordEtymIsQuestionable;
	}

	public List<WordEtymPOCRel> getWordEtymRelations() {
		return wordEtymRelations;
	}

	public void setWordEtymRelations(List<WordEtymPOCRel> wordEtymRelations) {
		this.wordEtymRelations = wordEtymRelations;
	}
}
