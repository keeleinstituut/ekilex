package eki.wordweb.data;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class TypeWordEtym extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long etymWordId;

	private String etymWord;

	private String etymWordLang;

	private Classifier etymWordLanguage;

	private String etymYear;

	private String[] etymMeaningWords;

	private String[] etymWordSources;

	private String[] comments;

	private boolean isQuestionable;

	private boolean isCompound;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getEtymWordId() {
		return etymWordId;
	}

	public void setEtymWordId(Long etymWordId) {
		this.etymWordId = etymWordId;
	}

	public String getEtymWord() {
		return etymWord;
	}

	public void setEtymWord(String etymWord) {
		this.etymWord = etymWord;
	}

	public String getEtymWordLang() {
		return etymWordLang;
	}

	public void setEtymWordLang(String etymWordLang) {
		this.etymWordLang = etymWordLang;
	}

	public Classifier getEtymWordLanguage() {
		return etymWordLanguage;
	}

	public void setEtymWordLanguage(Classifier etymWordLanguage) {
		this.etymWordLanguage = etymWordLanguage;
	}

	public String getEtymYear() {
		return etymYear;
	}

	public void setEtymYear(String etymYear) {
		this.etymYear = etymYear;
	}

	public String[] getEtymMeaningWords() {
		return etymMeaningWords;
	}

	public void setEtymMeaningWords(String[] etymMeaningWords) {
		this.etymMeaningWords = etymMeaningWords;
	}

	public String[] getEtymWordSources() {
		return etymWordSources;
	}

	public void setEtymWordSources(String[] etymWordSources) {
		this.etymWordSources = etymWordSources;
	}

	public String[] getComments() {
		return comments;
	}

	public void setComments(String[] comments) {
		this.comments = comments;
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
