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
	private String wordLang;

	@Column(name = "homonym_nr")
	private Integer homonymNr;

	@Column(name = "word")
	private String word;

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

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
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
