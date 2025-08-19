package eki.wordweb.data.os;

import java.util.Collections;
import java.util.List;

import eki.wordweb.data.AbstractSearchResult;

public class OsSearchResult extends AbstractSearchResult {

	private static final long serialVersionUID = 1L;

	private List<OsWord> words;

	private OsWord selectedWord;

	private boolean validSearch;

	public OsSearchResult() {
		super(false, false, 0);
		this.words = Collections.emptyList();
		this.selectedWord = null;
		this.validSearch = false;
	}

	public OsSearchResult(List<OsWord> words, OsWord selectedWord, boolean resultExists, boolean singleResult, int resultCount) {
		super(resultExists, singleResult, resultCount);
		this.words = words;
		this.selectedWord = selectedWord;
		this.validSearch = true;
	}

	public List<OsWord> getWords() {
		return words;
	}

	public OsWord getSelectedWord() {
		return selectedWord;
	}

	public boolean isValidSearch() {
		return validSearch;
	}

}
