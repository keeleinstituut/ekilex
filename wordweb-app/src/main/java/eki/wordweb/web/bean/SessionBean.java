package eki.wordweb.web.bean;

import eki.common.data.AbstractDataObject;

public class SessionBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String datasetType;

	private String sourceLang;

	private String destinLang;

	private String searchMode;

	private String lastSearchWord;

	public String getDatasetType() {
		return datasetType;
	}

	public void setDatasetType(String datasetType) {
		this.datasetType = datasetType;
	}

	public String getSourceLang() {
		return sourceLang;
	}

	public void setSourceLang(String sourceLang) {
		this.sourceLang = sourceLang;
	}

	public String getDestinLang() {
		return destinLang;
	}

	public void setDestinLang(String destinLang) {
		this.destinLang = destinLang;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
	}

	public String getLastSearchWord() {
		return lastSearchWord;
	}

	public void setLastSearchWord(String lastSearchWord) {
		this.lastSearchWord = lastSearchWord;
	}

}
