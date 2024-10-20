package eki.wordweb.data;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;
import eki.wordweb.data.type.TypeSourceLink;
import eki.wordweb.data.type.TypeWordEtymRelation;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WordEtymTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long wordEtymId;

	private Long wordEtymWordId;

	private String wordEtymWord;

	private String wordEtymWordLang;

	private Classifier wordEtymWordLanguage;

	private List<String> wordEtymWordMeaningWords;

	private String etymologyTypeCode;

	private Classifier etymologyType;

	private String etymologyYear;

	private String wordEtymComment;

	private boolean wordEtymIsQuestionable;

	private List<TypeWordEtymRelation> wordEtymRelations;

	private List<TypeSourceLink> sourceLinks;

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

	public Classifier getWordEtymWordLanguage() {
		return wordEtymWordLanguage;
	}

	public void setWordEtymWordLanguage(Classifier wordEtymWordLanguage) {
		this.wordEtymWordLanguage = wordEtymWordLanguage;
	}

	public List<String> getWordEtymWordMeaningWords() {
		return wordEtymWordMeaningWords;
	}

	public void setWordEtymWordMeaningWords(List<String> wordEtymWordMeaningWords) {
		this.wordEtymWordMeaningWords = wordEtymWordMeaningWords;
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

	public List<TypeWordEtymRelation> getWordEtymRelations() {
		return wordEtymRelations;
	}

	public void setWordEtymRelations(List<TypeWordEtymRelation> wordEtymRelations) {
		this.wordEtymRelations = wordEtymRelations;
	}

	public List<TypeSourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<TypeSourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
