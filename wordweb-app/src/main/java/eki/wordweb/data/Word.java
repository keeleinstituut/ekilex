package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Word extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String word;

	private Integer homonymNr;

	private String lang;

	private String morphCode;

	private String displayMorphCode;

	private List<String> datasetCodes;

	private Integer meaningCount;

	private List<TypeWord> meaningWords;

	private String meaningWordsWrapup;

	private List<TypeDefinition> definitions;

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

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

	public String getDisplayMorphCode() {
		return displayMorphCode;
	}

	public void setDisplayMorphCode(String displayMorphCode) {
		this.displayMorphCode = displayMorphCode;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

	public Integer getMeaningCount() {
		return meaningCount;
	}

	public void setMeaningCount(Integer meaningCount) {
		this.meaningCount = meaningCount;
	}

	public List<TypeWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<TypeWord> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public String getMeaningWordsWrapup() {
		return meaningWordsWrapup;
	}

	public void setMeaningWordsWrapup(String meaningWordsWrapup) {
		this.meaningWordsWrapup = meaningWordsWrapup;
	}

	public List<TypeDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<TypeDefinition> definitions) {
		this.definitions = definitions;
	}

}
