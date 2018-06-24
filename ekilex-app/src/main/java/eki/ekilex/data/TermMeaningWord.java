package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class TermMeaningWord extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private Long wordId;

	private String word;

	private Integer homonymNr;

	private String wordLang;

	private Long orderBy;

	private String datasetCodesWrapup;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

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

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public String getDatasetCodesWrapup() {
		return datasetCodesWrapup;
	}

	public void setDatasetCodesWrapup(String datasetCodes) {
		this.datasetCodesWrapup = datasetCodes;
	}

}
