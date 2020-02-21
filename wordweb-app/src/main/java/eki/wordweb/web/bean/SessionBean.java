package eki.wordweb.web.bean;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class SessionBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> destinLangs;

	private List<String> datasetCodes;

	private String searchMode;

	private String recentSearchMode;

	private String searchWord;

	private String recentWord;

	public List<String> getDestinLangs() {
		return destinLangs;
	}

	public void setDestinLangs(List<String> destinLangs) {
		this.destinLangs = destinLangs;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
	}

	public String getRecentSearchMode() {
		return recentSearchMode;
	}

	public void setRecentSearchMode(String recentSearchMode) {
		this.recentSearchMode = recentSearchMode;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public String getRecentWord() {
		return recentWord;
	}

	public void setRecentWord(String recentWord) {
		this.recentWord = recentWord;
	}

}
