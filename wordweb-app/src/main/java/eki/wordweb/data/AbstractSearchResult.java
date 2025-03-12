package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public abstract class AbstractSearchResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean resultExists;

	private boolean singleResult;

	private int resultCount;

	public AbstractSearchResult(boolean resultExists, boolean singleResult, int resultCount) {
		this.resultExists = resultExists;
		this.singleResult = singleResult;
		this.resultCount = resultCount;
	}

	public int getResultCount() {
		return resultCount;
	}

	public boolean isResultExists() {
		return resultExists;
	}

	public boolean isSingleResult() {
		return singleResult;
	}

}
