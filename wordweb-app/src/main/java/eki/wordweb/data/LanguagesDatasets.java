package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class LanguagesDatasets extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> languageCodes;

	private List<Classifier> languages;

	private List<String> datasetCodes;

	private List<Dataset> datasets;

	private boolean suggestionsExist;

	public List<String> getLanguageCodes() {
		return languageCodes;
	}

	public void setLanguageCodes(List<String> languageCodes) {
		this.languageCodes = languageCodes;
	}

	public List<Classifier> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Classifier> languages) {
		this.languages = languages;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

	public List<Dataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<Dataset> datasets) {
		this.datasets = datasets;
	}

	public boolean isSuggestionsExist() {
		return suggestionsExist;
	}

	public void setSuggestionsExist(boolean suggestionsExist) {
		this.suggestionsExist = suggestionsExist;
	}

}
