package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WordLexemeMeaningIdTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long meaningId;

	private Long lexemeId;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

}
