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

	private List<String> meaningWords;

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

	public List<String> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<String> meaningWords) {
		this.meaningWords = meaningWords;
	}

}
