package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public abstract class AbstractSearchResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private int resultCount;

	private boolean resultsExist;

	private boolean singleResult;

	public AbstractSearchResult(int resultCount, boolean resultsExist, boolean singleResult) {
		this.resultCount = resultCount;
		this.resultsExist = resultsExist;
		this.singleResult = singleResult;
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
