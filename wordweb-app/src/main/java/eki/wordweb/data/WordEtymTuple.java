package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class WordEtymTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long wordEtymWordId;

	private Long wordEtymId;

	private String etymologyTypeCode;

	private Classifier etymologyType;

	private String etymologyYear;

	private String wordEtymComment;

	private boolean wordEtymIsQuestionable;

	private List<String> wordEtymSources;

	private Long wordEtymRelId;

	private String wordEtymRelComment;

	private boolean wordEtymRelIsQuestionable;

	private boolean wordEtymRelIsCompound;

	private Long relatedWordId;

	private String relatedWord;

	private String relatedWordLang;

	private Classifier relatedWordLanguage;

	private List<String> meaningWords;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getWordEtymWordId() {
		return wordEtymWordId;
	}

	public void setWordEtymWordId(Long wordEtymWordId) {
		this.wordEtymWordId = wordEtymWordId;
	}

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

	public Classifier getEtymologyType() {
		return etymologyType;
	}

	public void setEtymologyType(Classifier etymologyType) {
		this.etymologyType = etymologyType;
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

	public List<String> getWordEtymSources() {
		return wordEtymSources;
	}

	public void setWordEtymSources(List<String> wordEtymSources) {
		this.wordEtymSources = wordEtymSources;
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

	public boolean isWordEtymRelIsQuestionable() {
		return wordEtymRelIsQuestionable;
	}

	public void setWordEtymRelIsQuestionable(boolean wordEtymRelIsQuestionable) {
		this.wordEtymRelIsQuestionable = wordEtymRelIsQuestionable;
	}

	public boolean isWordEtymRelIsCompound() {
		return wordEtymRelIsCompound;
	}

	public void setWordEtymRelIsCompound(boolean wordEtymRelIsCompound) {
		this.wordEtymRelIsCompound = wordEtymRelIsCompound;
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

	public Classifier getRelatedWordLanguage() {
		return relatedWordLanguage;
	}

	public void setRelatedWordLanguage(Classifier relatedWordLanguage) {
		this.relatedWordLanguage = relatedWordLanguage;
	}

	public List<String> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<String> meaningWords) {
		this.meaningWords = meaningWords;
	}

}
