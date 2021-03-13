package eki.wordweb.data;

import java.util.Collections;
import java.util.List;

public class WordsData {

	private final List<Word> wordMatchWords;

	private final List<String> formMatchWordValues;

	private final int resultCount;

	private final boolean resultsExist;

	private final boolean singleResult;

	public WordsData() {
		this.wordMatchWords = Collections.emptyList();
		this.formMatchWordValues = Collections.emptyList();
		this.resultCount = 0;
		this.resultsExist = false;
		this.singleResult = false;
	}

	public WordsData(List<Word> wordMatchWords, List<String> formMatchWordValues, int resultCount, boolean resultsExist, boolean singleResult) {
		this.wordMatchWords = wordMatchWords;
		this.formMatchWordValues = formMatchWordValues;
		this.resultCount = resultCount;
		this.resultsExist = resultsExist;
		this.singleResult = singleResult;
	}

	public List<Word> getWordMatchWords() {
		return wordMatchWords;
	}

	public List<String> getFormMatchWordValues() {
		return formMatchWordValues;
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
