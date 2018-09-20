package eki.wordweb.data;

import java.util.List;

public class WordsData {

	private final List<Word> fullMatchWords;

	private final List<String> formMatchWords;

	private final String searchMode;

	public WordsData(List<Word> fullMatchWords, List<String> formMatchWords, String searchMode) {
		this.fullMatchWords = fullMatchWords;
		this.formMatchWords = formMatchWords;
		this.searchMode = searchMode;
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

}
