package eki.wordweb.web.bean;

import eki.common.data.AbstractDataObject;

public class SessionBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String sourceLang;

	private String destinLang;

	private String searchMode;

	private String searchWord;

	private String recentWord;

	private Integer recentHomonymNr;

	public String getSourceLang() {
		return sourceLang;
	}

	public void setSourceLang(String sourceLang) {
		this.sourceLang = sourceLang;
	}

	public String getDestinLang() {
		return destinLang;
	}

	public void setDestinLang(String destinLang) {
		this.destinLang = destinLang;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
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

	public Integer getRecentHomonymNr() {
		return recentHomonymNr;
	}

	public void setRecentHomonymNr(Integer recentHomonymNr) {
		this.recentHomonymNr = recentHomonymNr;
	}

}
