package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class WordEtymology extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String etymologyYear;

	private String etymologyTypeCode;

	private Classifier etymologyType;

	private List<String> wordSources;

	private List<TypeWordEtym> etymLineup;

	private String wordEtymologyWrapup;

	private String wordEtymologyLineupWrapup;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getEtymologyYear() {
		return etymologyYear;
	}

	public void setEtymologyYear(String etymologyYear) {
		this.etymologyYear = etymologyYear;
	}

	public String getEtymologyTypeCode() {
		return etymologyTypeCode;
	}

	public void setEtymologyTypeCode(String etymologyTypeCode) {
		this.etymologyTypeCode = etymologyTypeCode;
	}

	public Classifier getEtymologyType() {
		return etymologyType;
	}

	public void setEtymologyType(Classifier etymologyType) {
		this.etymologyType = etymologyType;
	}

	public List<String> getWordSources() {
		return wordSources;
	}

	public void setWordSources(List<String> wordSources) {
		this.wordSources = wordSources;
	}

	public List<TypeWordEtym> getEtymLineup() {
		return etymLineup;
	}

	public void setEtymLineup(List<TypeWordEtym> etymLineup) {
		this.etymLineup = etymLineup;
	}

	public String getWordEtymologyWrapup() {
		return wordEtymologyWrapup;
	}

	public void setWordEtymologyWrapup(String wordEtymologyWrapup) {
		this.wordEtymologyWrapup = wordEtymologyWrapup;
	}

	public String getWordEtymologyLineupWrapup() {
		return wordEtymologyLineupWrapup;
	}

	public void setWordEtymologyLineupWrapup(String wordEtymologyLineupWrapup) {
		this.wordEtymologyLineupWrapup = wordEtymologyLineupWrapup;
	}

}
