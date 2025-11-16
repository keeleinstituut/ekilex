package eki.ekilex.data.api;

import java.util.List;

import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public class SynCandidacy extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String headwordValue;

	private String headwordLang;

	private String synCandidateDatasetCode;

	private List<SynCandidateWord> synCandidateWords;

	public String getHeadwordValue() {
		return headwordValue;
	}

	public void setHeadwordValue(String headwordValue) {
		this.headwordValue = headwordValue;
	}

	public String getHeadwordLang() {
		return headwordLang;
	}

	public void setHeadwordLang(String headwordLang) {
		this.headwordLang = headwordLang;
	}

	public String getSynCandidateDatasetCode() {
		return synCandidateDatasetCode;
	}

	public void setSynCandidateDatasetCode(String datasetCode) {
		this.synCandidateDatasetCode = datasetCode;
	}

	public List<SynCandidateWord> getSynCandidateWords() {
		return synCandidateWords;
	}

	public void setSynCandidateWords(List<SynCandidateWord> synCandidateWords) {
		this.synCandidateWords = synCandidateWords;
	}

}
