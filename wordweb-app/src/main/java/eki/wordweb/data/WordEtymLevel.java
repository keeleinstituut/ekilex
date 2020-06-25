package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class WordEtymLevel extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String word;

	private String lang;

	private Classifier language;

	private List<String> meaningWords;

	private String etymologyTypeCode;

	private Classifier etymologyType;

	private String etymYear;

	private boolean questionable;

	private boolean compound;

	private String comment;

	private List<String> sourceLinkValues;

	private String levelWrapup;

	private List<WordEtymLevel> tree;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Classifier getLanguage() {
		return language;
	}

	public void setLanguage(Classifier language) {
		this.language = language;
	}

	public List<String> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<String> meaningWords) {
		this.meaningWords = meaningWords;
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

	public String getEtymYear() {
		return etymYear;
	}

	public void setEtymYear(String etymYear) {
		this.etymYear = etymYear;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<String> getSourceLinkValues() {
		return sourceLinkValues;
	}

	public void setSourceLinkValues(List<String> sourceLinkValues) {
		this.sourceLinkValues = sourceLinkValues;
	}

	public String getLevelWrapup() {
		return levelWrapup;
	}

	public void setLevelWrapup(String levelWrapup) {
		this.levelWrapup = levelWrapup;
	}

	public List<WordEtymLevel> getTree() {
		return tree;
	}

	public void setTree(List<WordEtymLevel> tree) {
		this.tree = tree;
	}

}
