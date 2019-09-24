package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

//TODO rename after fully operational
public class TermMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private List<String> conceptIds;

	private List<TypeTermMeaningWord> meaningWords;

	private boolean meaningWordsExist;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public List<String> getConceptIds() {
		return conceptIds;
	}

	public void setConceptIds(List<String> conceptIds) {
		this.conceptIds = conceptIds;
	}

	public List<TypeTermMeaningWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<TypeTermMeaningWord> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public boolean isMeaningWordsExist() {
		return meaningWordsExist;
	}

	public void setMeaningWordsExist(boolean meaningWordsExist) {
		this.meaningWordsExist = meaningWordsExist;
	}

}
