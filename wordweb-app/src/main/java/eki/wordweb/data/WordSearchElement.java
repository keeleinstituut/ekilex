package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class WordSearchElement extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String sgroup;

	private String wordValue;

	private Integer homonymNr;

	public String getSgroup() {
		return sgroup;
	}

	public void setSgroup(String sgroup) {
		this.sgroup = sgroup;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

}
