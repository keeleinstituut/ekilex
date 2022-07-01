package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WordLexemeMeaningDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long lexemeId;

	private Long meaningId;

	private String wordValue;

	private String wordValuePrese;

	private String language;

	private String dataset;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public String getWordValuePrese() {
		return wordValuePrese;
	}

	public void setWordValuePrese(String wordValuePrese) {
		this.wordValuePrese = wordValuePrese;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

}
