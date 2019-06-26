package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordMeaningPair extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String word;

	private Long wordId;

	private Long meaningId;

	private List<Long> lexemeIds;

	public WordMeaningPair() {
	}

	public WordMeaningPair(String word, Long wordId, Long meaningId, List<Long> lexemeIds) {
		this.word = word;
		this.wordId = wordId;
		this.meaningId = meaningId;
		this.lexemeIds = lexemeIds;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
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