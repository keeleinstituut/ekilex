package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class CollocationRelGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String name;

	private Float frequency;

	private Float score;

	private List<Collocation> collocations;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public List<Collocation> getCollocations() {
		return collocations;
	}

	public void setCollocations(List<Collocation> collocations) {
		this.collocations = collocations;
	}

}
