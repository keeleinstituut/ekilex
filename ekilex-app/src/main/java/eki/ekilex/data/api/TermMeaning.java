package eki.ekilex.data.api;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private String datasetCode;

	private List<TermDefinition> definitions;

	private List<TermWord> words;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public List<TermDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<TermDefinition> definitions) {
		this.definitions = definitions;
	}

	public List<TermWord> getWords() {
		return words;
	}

	public void setWords(List<TermWord> words) {
		this.words = words;
	}

}
