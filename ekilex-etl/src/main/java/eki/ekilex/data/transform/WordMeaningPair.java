package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordMeaningPair extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String word;

	private Long wordId;

	private Long meaningId;

	private List<Long> lexemeIds;

	private boolean mainDatasetLexemeExists;

	private Integer mainDatasetLexemeMaxLevel1;

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

	public boolean isMainDatasetLexemeExists() {
		return mainDatasetLexemeExists;
	}

	public void setMainDatasetLexemeExists(boolean mainDatasetLexemeExists) {
		this.mainDatasetLexemeExists = mainDatasetLexemeExists;
	}

	public Integer getMainDatasetLexemeMaxLevel1() {
		return mainDatasetLexemeMaxLevel1;
	}

	public void setMainDatasetLexemeMaxLevel1(Integer mainDatasetLexemeMaxLevel1) {
		this.mainDatasetLexemeMaxLevel1 = mainDatasetLexemeMaxLevel1;
	}

}