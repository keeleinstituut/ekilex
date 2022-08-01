package eki.ekilex.data;

import java.util.List;

public class TermSearchResult extends PagingResult {

	private static final long serialVersionUID = 1L;

	private int meaningCount;

	private int wordCount;

	private int resultCount;

	private List<TermMeaning> results;

	private List<Long> resultMeaningIds;

	private boolean resultExist;

	private boolean resultDownloadNow;

	private boolean resultDownloadLater;

	public int getMeaningCount() {
		return meaningCount;
	}

	public void setMeaningCount(int meaningCount) {
		this.meaningCount = meaningCount;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public List<TermMeaning> getResults() {
		return results;
	}

	public void setResults(List<TermMeaning> results) {
		this.results = results;
	}

	public List<Long> getResultMeaningIds() {
		return resultMeaningIds;
	}

	public void setResultMeaningIds(List<Long> resultMeaningIds) {
		this.resultMeaningIds = resultMeaningIds;
	}

	public boolean isResultExist() {
		return resultExist;
	}

	public void setResultExist(boolean resultExist) {
		this.resultExist = resultExist;
	}

	public boolean isResultDownloadNow() {
		return resultDownloadNow;
	}

	public void setResultDownloadNow(boolean resultDownloadNow) {
		this.resultDownloadNow = resultDownloadNow;
	}

	public boolean isResultDownloadLater() {
		return resultDownloadLater;
	}

	public void setResultDownloadLater(boolean resultDownloadLater) {
		this.resultDownloadLater = resultDownloadLater;
	}

}
