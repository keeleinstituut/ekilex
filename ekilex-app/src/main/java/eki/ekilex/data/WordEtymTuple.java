package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

// should be replaced and removed at some point
@Deprecated
public class WordEtymTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordEtymId;

	private String etymologyTypeCode;

	private String etymologyYear;

	private String wordEtymComment;

	private boolean wordEtymQuestionable;

	private Long wordEtymSourceLinkId;

	private Long wordEtymSourceId;

	private String wordEtymSourceName;

	private Long wordEtymRelId;

	private String wordEtymRelComment;

	private boolean wordEtymRelQuestionable;

	private boolean wordEtymRelCompound;

	private Long relatedWordId;

	private String relatedWord;

	private String relatedWordLang;

	public Long getWordEtymId() {
		return wordEtymId;
	}

	public void setWordEtymId(Long wordEtymId) {
		this.wordEtymId = wordEtymId;
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

	public boolean isWordEtymQuestionable() {
		return wordEtymQuestionable;
	}

	public void setWordEtymQuestionable(boolean wordEtymQuestionable) {
		this.wordEtymQuestionable = wordEtymQuestionable;
	}

	public Long getWordEtymSourceLinkId() {
		return wordEtymSourceLinkId;
	}

	public void setWordEtymSourceLinkId(Long wordEtymSourceLinkId) {
		this.wordEtymSourceLinkId = wordEtymSourceLinkId;
	}

	public Long getWordEtymSourceId() {
		return wordEtymSourceId;
	}

	public void setWordEtymSourceId(Long wordEtymSourceId) {
		this.wordEtymSourceId = wordEtymSourceId;
	}

	public String getWordEtymSourceName() {
		return wordEtymSourceName;
	}

	public void setWordEtymSourceName(String wordEtymSourceName) {
		this.wordEtymSourceName = wordEtymSourceName;
	}

	public Long getWordEtymRelId() {
		return wordEtymRelId;
	}

	public void setWordEtymRelId(Long wordEtymRelId) {
		this.wordEtymRelId = wordEtymRelId;
	}

	public String getWordEtymRelComment() {
		return wordEtymRelComment;
	}

	public void setWordEtymRelComment(String wordEtymRelComment) {
		this.wordEtymRelComment = wordEtymRelComment;
	}

	public boolean isWordEtymRelQuestionable() {
		return wordEtymRelQuestionable;
	}

	public void setWordEtymRelQuestionable(boolean wordEtymRelQuestionable) {
		this.wordEtymRelQuestionable = wordEtymRelQuestionable;
	}

	public boolean isWordEtymRelCompound() {
		return wordEtymRelCompound;
	}

	public void setWordEtymRelCompound(boolean wordEtymRelCompound) {
		this.wordEtymRelCompound = wordEtymRelCompound;
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
