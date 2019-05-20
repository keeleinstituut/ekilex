package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordsResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private int totalCount;

	private List<Word> words;

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<Word> getWords() {
		return words;
	}

	public void setWords(List<Word> words) {
		this.words = words;
	}
}
