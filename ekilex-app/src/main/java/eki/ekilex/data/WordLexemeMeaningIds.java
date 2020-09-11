package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WordLexemeMeaningIds extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long[] lexemeIds;

	private Long[] wordIds;

	private Long[] meaningIds;

	public Long[] getLexemeIds() {
		return lexemeIds;
	}

	public void setLexemeIds(Long[] lexemeIds) {
		this.lexemeIds = lexemeIds;
	}

	public Long[] getWordIds() {
		return wordIds;
	}

	public void setWordIds(Long[] wordIds) {
		this.wordIds = wordIds;
	}

	public Long[] getMeaningIds() {
		return meaningIds;
	}

	public void setMeaningIds(Long[] meaningIds) {
		this.meaningIds = meaningIds;
	}

}
