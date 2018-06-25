package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class MeaningsResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private int meaningCount;

	private int wordCount;

	private List<TermMeaning> termMeanings;

	private boolean resultExist;

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
