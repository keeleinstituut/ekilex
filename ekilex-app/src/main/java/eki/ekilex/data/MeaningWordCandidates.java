package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class MeaningWordCandidates extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String wordValue;

	private boolean meaningHasWord;

	private List<WordDescript> wordCandidates;

	private boolean wordCandidatesExist;

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public boolean isMeaningHasWord() {
		return meaningHasWord;
	}

	public void setMeaningHasWord(boolean meaningHasWord) {
		this.meaningHasWord = meaningHasWord;
	}

	public List<WordDescript> getWordCandidates() {
		return wordCandidates;
	}

	public void setWordCandidates(List<WordDescript> wordCandidates) {
		this.wordCandidates = wordCandidates;
	}

	public boolean isWordCandidatesExist() {
		return wordCandidatesExist;
	}

	public void setWordCandidatesExist(boolean wordCandidatesExist) {
		this.wordCandidatesExist = wordCandidatesExist;
	}

}
