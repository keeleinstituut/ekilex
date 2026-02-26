package eki.ekimedia.data;

import java.time.LocalDate;
import java.util.List;

import eki.common.constant.RequestOrigin;
import eki.common.data.AbstractDataObject;

public class SearchFilteredCount extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String searchWord;

	private String searchMode;

	private List<String> destinLangs;

	private List<String> datasetCodes;

	private boolean resultExists;

	private RequestOrigin requestOrigin;

	private LocalDate searchDate;

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
	}

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

	public boolean isResultExists() {
		return resultExists;
	}

	public void setResultExists(boolean resultExists) {
		this.resultExists = resultExists;
	}

	public RequestOrigin getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(RequestOrigin requestOrigin) {
		this.requestOrigin = requestOrigin;
	}

	public LocalDate getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(LocalDate searchDate) {
		this.searchDate = searchDate;
	}

}
