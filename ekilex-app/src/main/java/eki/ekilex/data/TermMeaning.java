package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private String conceptId;

	private TermMeaningWord mainWord;

	private List<TermMeaningWord> otherWords;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	public TermMeaningWord getMainWord() {
		return mainWord;
	}

	public void setMainWord(TermMeaningWord mainWord) {
		this.mainWord = mainWord;
	}

	public List<TermMeaningWord> getOtherWords() {
		return otherWords;
	}

	public void setOtherWords(List<TermMeaningWord> otherWords) {
		this.otherWords = otherWords;
	}

}
