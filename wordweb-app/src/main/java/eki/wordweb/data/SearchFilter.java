package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class SearchFilter extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String searchWord;

	private List<String> destinLangs;

	private Integer homonymNr;

	private String searchMode;

	private String searchUri;

	private boolean valid;

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public List<String> getDestinLangs() {
		return destinLangs;
	}

	public void setDestinLangs(List<String> destinLangs) {
		this.destinLangs = destinLangs;
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

	public String getSearchUri() {
		return searchUri;
	}

	public void setSearchUri(String searchUri) {
		this.searchUri = searchUri;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
