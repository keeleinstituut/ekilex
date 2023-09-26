package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordsMatchResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> wordValues;

	private int resultCount;

	private boolean resultsExist;

	public WordsMatchResult(List<String> wordValues, int resultCount, boolean resultsExist) {
		this.wordValues = wordValues;
		this.resultCount = resultCount;
		this.resultsExist = resultsExist;
	}

	public List<String> getWordValues() {
		return wordValues;
	}

	public int getResultCount() {
		return resultCount;
	}

	public boolean isResultsExist() {
		return resultsExist;
	}

}
