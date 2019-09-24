package eki.ekilex.data;

import java.util.List;

public class MeaningsResult extends PagingResult {

	private static final long serialVersionUID = 1L;

	private int meaningCount;

	private int wordCount;

	private List<TermMeaning> meanings;

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

	public List<TermMeaning> getMeanings() {
		return meanings;
	}

	public void setMeanings(List<TermMeaning> meanings) {
		this.meanings = meanings;
	}

	public boolean isResultExist() {
		return resultExist;
	}

	public void setResultExist(boolean resultExist) {
		this.resultExist = resultExist;
	}

}
