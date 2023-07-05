package eki.ekilex.data.api;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private String datasetCode;

	private List<TermClassifier> domains;

	private List<TermDefinition> definitions;

	private List<TermFreeform> notes;

	private List<TermForum> forums;

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

	public List<TermClassifier> getDomains() {
		return domains;
	}

	public void setDomains(List<TermClassifier> domains) {
		this.domains = domains;
	}

	public List<TermDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<TermDefinition> definitions) {
		this.definitions = definitions;
	}

	public List<TermFreeform> getNotes() {
		return notes;
	}

	public void setNotes(List<TermFreeform> notes) {
		this.notes = notes;
	}

	public List<TermForum> getForums() {
		return forums;
	}

	public void setForums(List<TermForum> forums) {
		this.forums = forums;
	}

	public List<TermWord> getWords() {
		return words;
	}

	public void setWords(List<TermWord> words) {
		this.words = words;
	}
}
