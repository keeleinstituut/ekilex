package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class SearchFilter extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String searchWord;

	private String sourceLang;

	private String destinLang;

	private Integer homonymNr;

	private boolean beginner;

	private String searchUri;

	private boolean valid;

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

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

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public boolean isBeginner() {
		return beginner;
	}

	public void setBeginner(boolean beginner) {
		this.beginner = beginner;
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
