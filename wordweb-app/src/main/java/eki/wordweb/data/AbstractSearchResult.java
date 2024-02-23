package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public abstract class AbstractSearchResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean resultsExist;

	private boolean singleResult;

	private int resultCount;

	public AbstractSearchResult(boolean resultsExist, boolean singleResult, int resultCount) {
		this.resultsExist = resultsExist;
		this.singleResult = singleResult;
		this.resultCount = resultCount;
	}

	public int getResultCount() {
		return resultCount;
	}

	public boolean isResultsExist() {
		return resultsExist;
	}

	public boolean isSingleResult() {
		return singleResult;
	}

}
