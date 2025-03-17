package eki.wordweb.data;

import java.util.Collections;
import java.util.List;

public class WordsData extends AbstractSearchResult {

	private static final long serialVersionUID = 1L;

	private final List<Word> wordMatchWords;

	private final List<String> suggestedWordValues;

	private final boolean validSearch;

	private final boolean formResultExists;

	private final boolean altResultExists;

	private final boolean suggestionExists;

	private final LanguagesDatasets availableLanguagesDatasets;

	public WordsData() {
		super(false, false, 0);
		this.wordMatchWords = Collections.emptyList();
		this.suggestedWordValues = Collections.emptyList();
		this.validSearch = false;
		this.formResultExists = false;
		this.altResultExists = false;
		this.suggestionExists = false;
		this.availableLanguagesDatasets = new LanguagesDatasets();
	}

	public WordsData(List<Word> wordMatchWords, boolean wordResultExists, boolean wordSingleResult, int wordResultCount, List<String> suggestedWordValues, boolean formResultExists) {
		super(wordResultExists, wordSingleResult, wordResultCount);
		this.wordMatchWords = wordMatchWords;
		this.suggestedWordValues = suggestedWordValues;
		this.validSearch = true;
		this.formResultExists = formResultExists;
		this.altResultExists = false;
		this.suggestionExists = formResultExists;
		this.availableLanguagesDatasets = new LanguagesDatasets();
	}

	public WordsData(List<String> suggestedWordValues, boolean altResultExists) {
		super(false, false, 0);
		this.wordMatchWords = Collections.emptyList();
		this.suggestedWordValues = suggestedWordValues;
		this.validSearch = true;
		this.formResultExists = false;
		this.altResultExists = altResultExists;
		this.suggestionExists = altResultExists;
		this.availableLanguagesDatasets = new LanguagesDatasets();
	}

	public WordsData(LanguagesDatasets availableLanguagesDatasets) {
		super(false, false, 0);
		this.wordMatchWords = Collections.emptyList();
		this.suggestedWordValues = Collections.emptyList();
		this.validSearch = true;
		this.formResultExists = false;
		this.altResultExists = false;
		this.suggestionExists = false;
		this.availableLanguagesDatasets = availableLanguagesDatasets;
	}

	public List<Word> getWordMatchWords() {
		return wordMatchWords;
	}

	public List<String> getSuggestedWordValues() {
		return suggestedWordValues;
	}

	public boolean isValidSearch() {
		return validSearch;
	}

	public boolean isFormResultExists() {
		return formResultExists;
	}

	public boolean isAltResultExists() {
		return altResultExists;
	}

	public boolean isSuggestionExists() {
		return suggestionExists;
	}

	public LanguagesDatasets getAvailableLanguagesDatasets() {
		return availableLanguagesDatasets;
	}

}
