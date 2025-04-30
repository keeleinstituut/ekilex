package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class NewWord extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String word;

	private String wordPrese;

	private Integer homonymNr;

	private Integer regYear;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWordPrese() {
		return wordPrese;
	}

	public void setWordPrese(String wordPrese) {
		this.wordPrese = wordPrese;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public Integer getRegYear() {
		return regYear;
	}

	public void setRegYear(Integer regYear) {
		this.regYear = regYear;
	}

}
