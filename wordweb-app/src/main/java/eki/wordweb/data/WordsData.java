package eki.wordweb.data;

import java.util.Collections;
import java.util.List;

public class WordsData extends AbstractSearchResult {

	private static final long serialVersionUID = 1L;

	private final List<Word> wordMatchWords;

	private final List<String> suggestedWordValues;

	private final boolean formResultsExist;

	private final boolean altResultsExist;

	private final boolean suggestionsExist;

	private final LanguagesDatasets availableLanguagesDatasets;

	public WordsData() {
		super(false, false, 0);
		this.wordMatchWords = Collections.emptyList();
		this.suggestedWordValues = Collections.emptyList();
		this.formResultsExist = false;
		this.altResultsExist = false;
		this.suggestionsExist = false;
		this.availableLanguagesDatasets = new LanguagesDatasets();
	}

	public WordsData(List<Word> wordMatchWords, boolean wordResultsExist, boolean wordSingleResult, int wordResultCount, List<String> suggestedWordValues, boolean formResultsExist) {
		super(wordResultsExist, wordSingleResult, wordResultCount);
		this.wordMatchWords = wordMatchWords;
		this.suggestedWordValues = suggestedWordValues;
		this.formResultsExist = formResultsExist;
		this.altResultsExist = false;
		this.suggestionsExist = formResultsExist;
		this.availableLanguagesDatasets = new LanguagesDatasets();
	}

	public WordsData(List<String> suggestedWordValues, boolean altResultsExist) {
		super(false, false, 0);
		this.wordMatchWords = Collections.emptyList();
		this.suggestedWordValues = suggestedWordValues;
		this.formResultsExist = false;
		this.altResultsExist = altResultsExist;
		this.suggestionsExist = altResultsExist;
		this.availableLanguagesDatasets = new LanguagesDatasets();
	}

	public WordsData(LanguagesDatasets availableLanguagesDatasets) {
		super(false, false, 0);
		this.wordMatchWords = Collections.emptyList();
		this.suggestedWordValues = Collections.emptyList();
		this.formResultsExist = false;
		this.altResultsExist = false;
		this.suggestionsExist = false;
		this.availableLanguagesDatasets = availableLanguagesDatasets;
	}

	public List<Word> getWordMatchWords() {
		return wordMatchWords;
	}

	public List<String> getSuggestedWordValues() {
		return suggestedWordValues;
	}

	public boolean isFormResultsExist() {
		return formResultsExist;
	}

	public boolean isAltResultsExist() {
		return altResultsExist;
	}

	public boolean isSuggestionsExist() {
		return suggestionsExist;
	}

	public LanguagesDatasets getAvailableLanguagesDatasets() {
		return availableLanguagesDatasets;
	}

}
