package eki.wordweb.data.od;

import java.util.Collections;
import java.util.List;

import eki.wordweb.data.AbstractSearchResult;

public class OdSearchResult extends AbstractSearchResult {

	private static final long serialVersionUID = 1L;

	private List<OdWord> words;

	private OdWord selectedWord;

	private boolean validSearch;

	public OdSearchResult() {
		super(false, false, 0);
		this.words = Collections.emptyList();
		this.selectedWord = null;
		this.validSearch = false;
	}

	public OdSearchResult(List<OdWord> words, OdWord selectedWord, boolean resultExists, boolean singleResult, int resultCount) {
		super(resultExists, singleResult, resultCount);
		this.words = words;
		this.selectedWord = selectedWord;
		this.validSearch = true;
	}

	public List<OdWord> getWords() {
		return words;
	}

	public OdWord getSelectedWord() {
		return selectedWord;
	}

	public boolean isValidSearch() {
		return validSearch;
	}

}
