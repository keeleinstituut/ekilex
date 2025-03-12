package eki.common.data;

import java.util.List;

import eki.common.constant.RequestOrigin;

public class SearchStat extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String searchWord;

	private Integer homonymNr;

	private String searchMode;

	private List<String> destinLangs;

	private List<String> datasetCodes;

	private String searchUri;

	private int resultCount;

	private boolean resultExists;

	private boolean singleResult;

	private String userAgent;

	private String referrerDomain;

	private String sessionId;

	private RequestOrigin requestOrigin;

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

	public String getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
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

	public String getSearchUri() {
		return searchUri;
	}

	public void setSearchUri(String searchUri) {
		this.searchUri = searchUri;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public boolean isResultExists() {
		return resultExists;
	}

	public void setResultExists(boolean resultExists) {
		this.resultExists = resultExists;
	}

	public boolean isSingleResult() {
		return singleResult;
	}

	public void setSingleResult(boolean singleResult) {
		this.singleResult = singleResult;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getReferrerDomain() {
		return referrerDomain;
	}

	public void setReferrerDomain(String referrerDomain) {
		this.referrerDomain = referrerDomain;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public RequestOrigin getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(RequestOrigin requestOrigin) {
		this.requestOrigin = requestOrigin;
	}
}
