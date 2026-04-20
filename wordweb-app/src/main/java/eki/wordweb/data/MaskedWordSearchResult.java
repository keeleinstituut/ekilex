package eki.wordweb.data;

import java.util.List;

public class MaskedWordSearchResult extends AbstractSearchResult {

	private static final long serialVersionUID = 1L;

	private List<String> wordValues;

	public MaskedWordSearchResult(List<String> wordValues, boolean resultExists, boolean singleResult, int resultCount) {
		super(resultExists, singleResult, resultCount);
		this.wordValues = wordValues;
	}

	public List<String> getWordValues() {
		return wordValues;
	}

}
