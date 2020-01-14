package eki.wordweb.data;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class TypeGrammar extends AbstractDataObject implements ComplexityType {

	private static final long serialVersionUID = 1L;

	private String value;

	private Complexity complexity;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

}
