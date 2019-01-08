package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordEtymology extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private List<String> wordSources;

	private List<TypeWordEtym> etymLineup;

	private String wordEtymologyWrapup;

	private List<String> wordEtymologyLineupWrapup;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
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

	public List<String> getWordEtymologyLineupWrapup() {
		return wordEtymologyLineupWrapup;
	}

	public void setWordEtymologyLineupWrapup(List<String> wordEtymologyLineupWrapup) {
		this.wordEtymologyLineupWrapup = wordEtymologyLineupWrapup;
	}

}
