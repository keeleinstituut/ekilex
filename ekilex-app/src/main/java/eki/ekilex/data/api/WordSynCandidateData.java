package eki.ekilex.data.api;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordSynCandidateData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String lang;

	private String datasetCode;

	private List<WordSynCandidate> wordSynCandidates;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public List<WordSynCandidate> getWordSynCandidates() {
		return wordSynCandidates;
	}

	public void setWordSynCandidates(List<WordSynCandidate> wordSynCandidates) {
		this.wordSynCandidates = wordSynCandidates;
	}

}
