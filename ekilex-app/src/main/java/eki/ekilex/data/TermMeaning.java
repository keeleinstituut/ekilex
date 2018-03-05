package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private List<WordTuple> wordTuples;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public List<WordTuple> getWordTuples() {
		return wordTuples;
	}

	public void setWordTuples(List<WordTuple> wordTuples) {
		this.wordTuples = wordTuples;
	}

}
