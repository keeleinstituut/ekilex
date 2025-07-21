package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private List<Classifier> meaningDomains;

	private List<TermMeaningWord> meaningWords;

	private boolean meaningWordsExist;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public List<Classifier> getMeaningDomains() {
		return meaningDomains;
	}

	public void setMeaningDomains(List<Classifier> meaningDomains) {
		this.meaningDomains = meaningDomains;
	}

	public List<TermMeaningWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<TermMeaningWord> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public boolean isMeaningWordsExist() {
		return meaningWordsExist;
	}

	public void setMeaningWordsExist(boolean meaningWordsExist) {
		this.meaningWordsExist = meaningWordsExist;
	}

}
