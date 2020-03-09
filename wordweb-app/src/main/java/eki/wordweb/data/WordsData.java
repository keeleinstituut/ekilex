package eki.wordweb.data;

import java.util.Collections;
import java.util.List;

public class WordsData {

	private final List<Word> fullMatchWords;

	private final List<String> formMatchWords;

	private final int resultCount;

	private final boolean resultsExist;

	private final boolean singleResult;

	public WordsData() {
		this.fullMatchWords = Collections.emptyList();
		this.formMatchWords = Collections.emptyList();
		this.resultCount = 0;
		this.resultsExist = false;
		this.singleResult = false;
	}

	public WordsData(List<Word> fullMatchWords, List<String> formMatchWords, int resultCount, boolean resultsExist, boolean singleResult) {
		this.fullMatchWords = fullMatchWords;
		this.formMatchWords = formMatchWords;
		this.resultCount = resultCount;
		this.resultsExist = resultsExist;
		this.singleResult = singleResult;
	}

	public List<Word> getFullMatchWords() {
		return fullMatchWords;
	}

	public List<String> getFormMatchWords() {
		return formMatchWords;
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
