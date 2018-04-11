package eki.wordweb.data;

import java.util.List;

public class WordsData {

	private final List<Word> fullWords;

	private final List<Word> partialWords;

	public WordsData(List<Word> fullWords, List<Word> partialWords) {
		this.fullWords = fullWords;
		this.partialWords = partialWords;
	}

	public List<Word> getFullWords() {
		return fullWords;
	}

	public List<Word> getPartialWords() {
		return partialWords;
	}

}
