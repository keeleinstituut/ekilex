package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class WordTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "meaning_id")
	private Long meaningId;

	@Column(name = "word_id")
	private Long wordId;

	@Column(name = "word_lang")
	private String language;

	@Column(name = "homonym_nr")
	private Integer homonymNumber;

	@Column(name = "word")
	private String value;

	@Column(name = "dataset_codes")
	private String[] datasetCodes;

	@Column(name = "concept_id")
	private String conceptId;

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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Integer getHomonymNumber() {
		return homonymNumber;
	}

	public void setHomonymNumber(Integer homonymNumber) {
		this.homonymNumber = homonymNumber;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String[] getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(String[] datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

}
