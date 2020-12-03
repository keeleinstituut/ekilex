package eki.common.data;

import java.util.List;

public class SearchStat extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String searchWord;

	private Integer homonymNr;

	private String searchMode;

	private List<String> destinLangs;

	private List<String> datasetCodes;

	private String searchUri;

	private int resultCount;

	private boolean resultsExist;

	private boolean singleResult;

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
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

	public String getSearchUri() {
		return searchUri;
	}

	public void setSearchUri(String searchUri) {
		this.searchUri = searchUri;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public boolean isResultsExist() {
		return resultsExist;
	}

	public void setResultsExist(boolean resultsExist) {
		this.resultsExist = resultsExist;
	}

	public boolean isSingleResult() {
		return singleResult;
	}

	public void setSingleResult(boolean singleResult) {
		this.singleResult = singleResult;
	}
}
