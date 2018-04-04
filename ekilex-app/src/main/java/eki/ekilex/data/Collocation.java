package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Collocation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private Float frequency;

	private Float score;

	private List<String> collocUsages;

	private List<TypeCollocWord> collocWords;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Float getFrequency() {
		return frequency;
	}

	public void setFrequency(Float frequency) {
		this.frequency = frequency;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public List<String> getCollocUsages() {
		return collocUsages;
	}

	public void setCollocUsages(List<String> collocUsages) {
		this.collocUsages = collocUsages;
	}

	public List<TypeCollocWord> getCollocWords() {
		return collocWords;
	}

	public void setCollocWords(List<TypeCollocWord> collocWords) {
		this.collocWords = collocWords;
	}

}
