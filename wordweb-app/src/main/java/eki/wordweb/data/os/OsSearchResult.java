package eki.wordweb.data.os;

import java.util.Collections;
import java.util.List;

import eki.wordweb.data.AbstractSearchResult;

public class OsSearchResult extends AbstractSearchResult {

	private static final long serialVersionUID = 1L;

	private List<OsWord> words;

	private OsWord selectedWord;

	private boolean homonymSearch;

	private boolean compoundSearch;

	private boolean validSearch;

	public OsSearchResult() {
		super(false, false, 0);
		this.words = Collections.emptyList();
		this.selectedWord = null;
		this.validSearch = false;
		this.homonymSearch = true;
		this.compoundSearch = false;
	}

	public OsSearchResult(
			List<OsWord> words,
			OsWord selectedWord,
			boolean homonymSearch,
			boolean compoundSearch,
			boolean resultExists,
			boolean singleResult,
			int resultCount) {
		super(resultExists, singleResult, resultCount);
		this.words = words;
		this.selectedWord = selectedWord;
		this.homonymSearch = homonymSearch;
		this.compoundSearch = compoundSearch;
		this.validSearch = true;
	}

	public List<OsWord> getWords() {
		return words;
	}

	public OsWord getSelectedWord() {
		return selectedWord;
	}

	public boolean isHomonymSearch() {
		return homonymSearch;
	}

	public boolean isCompoundSearch() {
		return compoundSearch;
	}

	public boolean isValidSearch() {
		return validSearch;
	}

}
