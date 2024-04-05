package eki.ekilex.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class Collocation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String definition;

	private Float frequency;

	private Float score;

	private List<String> collocUsages;

	private List<CollocMember> collocMembers;

	private Complexity complexity;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
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

	public List<CollocMember> getCollocMembers() {
		return collocMembers;
	}

	public void setCollocMembers(List<CollocMember> collocMembers) {
		this.collocMembers = collocMembers;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

}
