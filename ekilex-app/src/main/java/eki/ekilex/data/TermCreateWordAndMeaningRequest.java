package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class TermCreateWordAndMeaningRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private String wordValue;

	private String language;

	private String datasetCode;

	private String backUri;

	private boolean clearResults;

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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getBackUri() {
		return backUri;
	}

	public void setBackUri(String backUri) {
		this.backUri = backUri;
	}

	public boolean isClearResults() {
		return clearResults;
	}

	public void setClearResults(boolean clearResults) {
		this.clearResults = clearResults;
	}
}
