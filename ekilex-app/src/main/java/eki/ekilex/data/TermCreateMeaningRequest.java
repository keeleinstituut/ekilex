package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class TermCreateMeaningRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private String language;

	private String dataset;

	private String searchUri;

	private boolean clearResults;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
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

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getSearchUri() {
		return searchUri;
	}

	public void setSearchUri(String searchUri) {
		this.searchUri = searchUri;
	}

	public boolean isClearResults() {
		return clearResults;
	}

	public void setClearResults(boolean clearResults) {
		this.clearResults = clearResults;
	}
}
