package eki.common.data;

import java.util.List;

public class StatSearchResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<ValueCount> valueCounts;

	private boolean resultsExist;

	public List<ValueCount> getValueCounts() {
		return valueCounts;
	}

	public void setValueCounts(List<ValueCount> valueCounts) {
		this.valueCounts = valueCounts;
	}

	public boolean isResultsExist() {
		return resultsExist;
	}

	public void setResultsExist(boolean resultsExist) {
		this.resultsExist = resultsExist;
	}

}
