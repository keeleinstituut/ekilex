package eki.wwexam.web.bean;

import eki.common.data.AbstractDataObject;

public class SessionBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String searchWord;

	private String recentWord;

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

}
