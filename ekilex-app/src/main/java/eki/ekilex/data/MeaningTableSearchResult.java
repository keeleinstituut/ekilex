package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class MeaningTableSearchResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private int resultCount;

	private List<MeaningTableRow> results;

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public List<MeaningTableRow> getResults() {
		return results;
	}

	public void setResults(List<MeaningTableRow> results) {
		this.results = results;
	}

}
