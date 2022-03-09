package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class WordSearchElement extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String sgroup;

	private String word;

	private Integer homonymNr;

	public String getSgroup() {
		return sgroup;
	}

	public void setSgroup(String sgroup) {
		this.sgroup = sgroup;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

}
