package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class SearchRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private SearchValidation searchValidation;

	private WordsData wordsData;

	private boolean isSearchForm;

	private String searchMode;

	private String sessionId;

	private String userAgent;

	private String referer;

	private String serverDomain;

	public SearchValidation getSearchValidation() {
		return searchValidation;
	}

	public void setSearchValidation(SearchValidation searchValidation) {
		this.searchValidation = searchValidation;
	}

	public WordsData getWordsData() {
		return wordsData;
	}

	public void setWordsData(WordsData wordsData) {
		this.wordsData = wordsData;
	}

	public boolean isSearchForm() {
		return isSearchForm;
	}

	public void setSearchForm(boolean searchForm) {
		isSearchForm = searchForm;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public String getServerDomain() {
		return serverDomain;
	}

	public void setServerDomain(String serverDomain) {
		this.serverDomain = serverDomain;
	}
}
