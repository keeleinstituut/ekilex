package eki.ekimedia.data;

import eki.common.constant.RequestOrigin;
import eki.common.data.AbstractDataObject;

public class SearchDefaultCount extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String searchWord;

	private boolean resultExists;

	private RequestOrigin requestOrigin;

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public boolean isResultExists() {
		return resultExists;
	}

	public void setResultExists(boolean resultExists) {
		this.resultExists = resultExists;
	}

	public RequestOrigin getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(RequestOrigin requestOrigin) {
		this.requestOrigin = requestOrigin;
	}

}
