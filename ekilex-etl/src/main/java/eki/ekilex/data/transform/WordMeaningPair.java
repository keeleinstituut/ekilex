package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordMeaningPair extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long meaningId;

	private List<Long> lexemeIds;

	public WordMeaningPair() {
	}

	public WordMeaningPair(Long wordId, Long meaningId, List<Long> lexemeIds) {
		this.wordId = wordId;
		this.meaningId = meaningId;
		this.lexemeIds = lexemeIds;
	}

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

	public List<Long> getLexemeIds() {
		return lexemeIds;
	}

	public void setLexemeIds(List<Long> lexemeIds) {
		this.lexemeIds = lexemeIds;
	}
}