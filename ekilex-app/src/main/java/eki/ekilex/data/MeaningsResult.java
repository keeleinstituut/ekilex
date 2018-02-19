package eki.ekilex.data;

import java.util.List;

public class MeaningsResult {

	private int resultCount;

	private List<TermMeaning> termMeanings;

	private boolean resultExist;

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public List<TermMeaning> getTermMeanings() {
		return termMeanings;
	}

	public void setTermMeanings(List<TermMeaning> termMeanings) {
		this.termMeanings = termMeanings;
	}

	public boolean isResultExist() {
		return resultExist;
	}

	public void setResultExist(boolean resultExist) {
		this.resultExist = resultExist;
	}

}
