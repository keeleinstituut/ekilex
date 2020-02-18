package eki.ekilex.data;

import java.util.List;

public class WordsSynResult extends PagingResult {

	private static final long serialVersionUID = 1L;

	private int totalCount;

	private List<SynWord> words;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<SynWord> getWords() {
		return words;
	}

	public void setWords(List<SynWord> words) {
		this.words = words;
	}
}
