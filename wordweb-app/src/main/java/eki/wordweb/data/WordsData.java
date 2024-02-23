package eki.wordweb.data;

import java.util.Collections;
import java.util.List;

public class WordsData extends AbstractSearchResult {

	private static final long serialVersionUID = 1L;

	private final List<Word> wordMatchWords;

	private final List<String> formMatchWordValues;

	private final boolean formResultsExist;

	private final LanguagesDatasets availableLanguagesDatasets;

	public WordsData() {
		super(false, false, 0);
		this.wordMatchWords = Collections.emptyList();
		this.formMatchWordValues = Collections.emptyList();
		this.formResultsExist = false;
		this.availableLanguagesDatasets = null;
	}

	public WordsData(List<Word> wordMatchWords, boolean wordResultsExist, boolean wordSingleResult, int wordResultCount, List<String> formMatchWordValues, boolean formResultsExist) {
		super(wordResultsExist, wordSingleResult, wordResultCount);
		this.wordMatchWords = wordMatchWords;
		this.formMatchWordValues = formMatchWordValues;
		this.formResultsExist = formResultsExist;
		this.availableLanguagesDatasets = null;
	}

	public WordsData(LanguagesDatasets availableLanguagesDatasets) {
		super(false, false, 0);
		this.wordMatchWords = Collections.emptyList();
		this.formMatchWordValues = Collections.emptyList();
		this.formResultsExist = false;
		this.availableLanguagesDatasets = availableLanguagesDatasets;
	}

	public List<Word> getWordMatchWords() {
		return wordMatchWords;
	}

	public List<String> getFormMatchWordValues() {
		return formMatchWordValues;
	}

	public boolean isFormResultsExist() {
		return formResultsExist;
	}

	public LanguagesDatasets getAvailableLanguagesDatasets() {
		return availableLanguagesDatasets;
	}

}
