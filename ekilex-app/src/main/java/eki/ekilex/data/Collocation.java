package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Collocation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long collocationWordId;

	private String value;

	private Float frequency;

	private Float score;

	private List<String> collocationUsages;

	public Long getCollocationWordId() {
		return collocationWordId;
	}

	public void setCollocationWordId(Long collocationWordId) {
		this.collocationWordId = collocationWordId;
	}

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

	public List<String> getCollocationUsages() {
		return collocationUsages;
	}

	public void setCollocationUsages(List<String> collocationUsages) {
		this.collocationUsages = collocationUsages;
	}

}
