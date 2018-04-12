package eki.wordweb.data;

import java.util.List;

public class WordsData {

	private final List<Word> fullMatchWords;

	private final List<String> formMatchWords;

	public WordsData(List<Word> fullMatchWords, List<String> formMatchWords) {
		this.fullMatchWords = fullMatchWords;
		this.formMatchWords = formMatchWords;
	}

	public List<Word> getFullMatchWords() {
		return fullMatchWords;
	}

	public List<String> getFormMatchWords() {
		return formMatchWords;
	}

}
