package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordLexemeMeaningIds extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Long> lexemeIds;

	private List<Long> wordIds;

	private List<Long> meaningIds;

	public List<Long> getLexemeIds() {
		return lexemeIds;
	}

	public void setLexemeIds(List<Long> lexemeIds) {
		this.lexemeIds = lexemeIds;
	}

	public List<Long> getWordIds() {
		return wordIds;
	}

	public void setWordIds(List<Long> wordIds) {
		this.wordIds = wordIds;
	}

	public List<Long> getMeaningIds() {
		return meaningIds;
	}

	public void setMeaningIds(List<Long> meaningIds) {
		this.meaningIds = meaningIds;
	}
}
