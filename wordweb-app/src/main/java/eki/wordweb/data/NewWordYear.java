package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class NewWordYear extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Integer regYear;

	private List<NewWord> newWords;

	public NewWordYear() {
	}

	public NewWordYear(Integer regYear, List<NewWord> newWords) {
		this.regYear = regYear;
		this.newWords = newWords;
	}

	public Integer getRegYear() {
		return regYear;
	}

	public void setRegYear(Integer regYear) {
		this.regYear = regYear;
	}

	public List<NewWord> getNewWords() {
		return newWords;
	}

	public void setNewWords(List<NewWord> newWords) {
		this.newWords = newWords;
	}

}
