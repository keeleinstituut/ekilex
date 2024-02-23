package eki.wordweb.data;

import java.util.List;

public class WordsMatch extends AbstractSearchResult {

	private static final long serialVersionUID = 1L;

	private List<String> wordValues;

	public WordsMatch(List<String> wordValues, boolean resultsExist, boolean singleResult, int resultCount) {
		super(resultsExist, singleResult, resultCount);
		this.wordValues = wordValues;
	}

	public List<String> getWordValues() {
		return wordValues;
	}

}
