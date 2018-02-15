package eki.ekilex.data;

import java.util.Collections;
import java.util.List;

public class WordsResult {

	private int totalCount = 0;

	private List<Word> words = Collections.emptyList();

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
