package eki.wordweb.data;

import java.util.Collections;
import java.util.List;

public class WordsData {

	private final List<Word> fullMatchWords;

	private final List<String> formMatchWords;

	private final String searchMode;

	private final boolean resultsExist;

	private final boolean singleResult;

	public WordsData(String searchMode) {
		this.fullMatchWords = Collections.emptyList();
		this.formMatchWords = Collections.emptyList();
		this.searchMode = searchMode;
		this.resultsExist = false;
		this.singleResult = false;
	}

	public WordsData(List<Word> fullMatchWords, List<String> formMatchWords, String searchMode, boolean resultsExist, boolean singleResult) {
		this.fullMatchWords = fullMatchWords;
		this.formMatchWords = formMatchWords;
		this.searchMode = searchMode;
		this.resultsExist = resultsExist;
		this.singleResult = singleResult;
	}

	public List<Word> getFullMatchWords() {
		return fullMatchWords;
	}

	public List<String> getFormMatchWords() {
		return formMatchWords;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public boolean isResultsExist() {
		return resultsExist;
	}

	public boolean isSingleResult() {
		return singleResult;
	}

}
