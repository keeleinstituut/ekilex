package eki.wwexam.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class SearchFilter extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> destinLangs;

	private List<String> datasetCodes;

	private String searchWord;

	private Integer homonymNr;

	private String lang;

	public SearchFilter() {
	}

	public SearchFilter(List<String> destinLangs, List<String> datasetCodes) {
		this.destinLangs = destinLangs;
		this.datasetCodes = datasetCodes;
	}

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

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
