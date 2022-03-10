package eki.wordweb.web.bean;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class SessionBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> destinLangs;

	private List<String> datasetCodes;

	private String searchWord;

	private String recentWord;

	private Long linkedLexemeId;

	public List<String> getDestinLangs() {
		return destinLangs;
	}

	public void setDestinLangs(List<String> destinLangs) {
		this.destinLangs = destinLangs;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public String getRecentWord() {
		return recentWord;
	}

	public void setRecentWord(String recentWord) {
		this.recentWord = recentWord;
	}

	public Long getLinkedLexemeId() {
		return linkedLexemeId;
	}

	public void setLinkedLexemeId(Long linkedLexemeId) {
		this.linkedLexemeId = linkedLexemeId;
	}

}
