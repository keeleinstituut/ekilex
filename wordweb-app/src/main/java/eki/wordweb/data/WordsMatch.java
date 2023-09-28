package eki.wordweb.data;

import java.util.List;

public class WordsMatch extends AbstractSearchResult {

	private static final long serialVersionUID = 1L;

	private List<String> wordValues;

	public WordsMatch(List<String> wordValues, int resultCount, boolean resultsExist, boolean singleResult) {
		super(resultCount, resultsExist, singleResult);
		this.wordValues = wordValues;
	}

	public List<String> getWordValues() {
		return wordValues;
	}

}
