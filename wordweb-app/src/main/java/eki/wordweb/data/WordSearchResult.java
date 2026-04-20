package eki.wordweb.data;

import java.util.Collections;
import java.util.List;

public class WordSearchResult extends AbstractSearchResult {

	private static final long serialVersionUID = 1L;

	private final List<Word> wordMatchWords;

	private final List<String> suggestedWordValues;

	private final WordData selectedWordData;

	private final boolean validSearch;

	private final boolean formResultExists;

	private final boolean altResultExists;

	private final boolean suggestionExists;

	private final LanguagesDatasets availableLanguagesDatasets;

	public WordSearchResult() {

		super(false, false, 0);

		this.wordMatchWords = Collections.emptyList();
		this.suggestedWordValues = Collections.emptyList();
		this.selectedWordData = null;
		this.validSearch = false;
		this.formResultExists = false;
		this.altResultExists = false;
		this.suggestionExists = false;
		this.availableLanguagesDatasets = new LanguagesDatasets();
	}

	public WordSearchResult(
			List<Word> wordMatchWords,
			List<String> suggestedWordValues,
			WordData selectedWordData,
			boolean wordResultExists,
			boolean wordSingleResult,
			int wordResultCount,
			boolean formResultExists) {

		super(wordResultExists, wordSingleResult, wordResultCount);

		this.wordMatchWords = wordMatchWords;
		this.suggestedWordValues = suggestedWordValues;
		this.selectedWordData = selectedWordData;
		this.validSearch = true;
		this.formResultExists = formResultExists;
		this.altResultExists = false;
		this.suggestionExists = formResultExists;
		this.availableLanguagesDatasets = new LanguagesDatasets();
	}

	public WordSearchResult(
			List<String> suggestedWordValues,
			boolean altResultExists) {

		super(false, false, 0);

		this.wordMatchWords = Collections.emptyList();
		this.suggestedWordValues = suggestedWordValues;
		this.selectedWordData = null;
		this.validSearch = true;
		this.formResultExists = false;
		this.altResultExists = altResultExists;
		this.suggestionExists = altResultExists;
		this.availableLanguagesDatasets = new LanguagesDatasets();
	}

	public WordSearchResult(LanguagesDatasets availableLanguagesDatasets) {

		super(false, false, 0);

		this.wordMatchWords = Collections.emptyList();
		this.suggestedWordValues = Collections.emptyList();
		this.selectedWordData = null;
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

	public WordData getSelectedWordData() {
		return selectedWordData;
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
