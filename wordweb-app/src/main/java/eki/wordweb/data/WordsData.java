package eki.wordweb.data;

import java.util.Collections;
import java.util.List;

public class WordsData extends AbstractSearchResult {

	private static final long serialVersionUID = 1L;

	private final List<Word> wordMatchWords;

	private final List<String> formMatchWordValues;

	private final LanguagesDatasets availableLanguagesDatasets;

	public WordsData() {
		super(0, false, false);
		this.wordMatchWords = Collections.emptyList();
		this.formMatchWordValues = Collections.emptyList();
		this.availableLanguagesDatasets = null;
	}

	public WordsData(List<Word> wordMatchWords, List<String> formMatchWordValues, int resultCount, boolean resultsExist, boolean singleResult) {
		super(resultCount, resultsExist, singleResult);
		this.wordMatchWords = wordMatchWords;
		this.formMatchWordValues = formMatchWordValues;
		this.availableLanguagesDatasets = null;
	}

	public WordsData(LanguagesDatasets availableLanguagesDatasets) {
		super(0, false, false);
		this.wordMatchWords = Collections.emptyList();
		this.formMatchWordValues = Collections.emptyList();
		this.availableLanguagesDatasets = availableLanguagesDatasets;
	}

	public List<Word> getWordMatchWords() {
		return wordMatchWords;
	}

	public List<String> getFormMatchWordValues() {
		return formMatchWordValues;
	}

	public LanguagesDatasets getAvailableLanguagesDatasets() {
		return availableLanguagesDatasets;
	}

}
