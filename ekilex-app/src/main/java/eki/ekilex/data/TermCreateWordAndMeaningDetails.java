package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermCreateWordAndMeaningDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private List<Definition> meaningDefinitions;

	private List<OrderedClassifier> meaningDomains;

	private String datasetCode;

	private String datasetName;

	private List<Classifier> datasetLanguages;

	private String wordValue;

	private String language;

	private List<WordDescript> wordCandidates;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public List<Definition> getMeaningDefinitions() {
		return meaningDefinitions;
	}

	public void setMeaningDefinitions(List<Definition> meaningDefinitions) {
		this.meaningDefinitions = meaningDefinitions;
	}

	public List<OrderedClassifier> getMeaningDomains() {
		return meaningDomains;
	}

	public void setMeaningDomains(List<OrderedClassifier> meaningDomains) {
		this.meaningDomains = meaningDomains;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public List<Classifier> getDatasetLanguages() {
		return datasetLanguages;
	}

	public void setDatasetLanguages(List<Classifier> datasetLanguages) {
		this.datasetLanguages = datasetLanguages;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<WordDescript> getWordCandidates() {
		return wordCandidates;
	}

	public void setWordCandidates(List<WordDescript> wordCandidates) {
		this.wordCandidates = wordCandidates;
	}
}
