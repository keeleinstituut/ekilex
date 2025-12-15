package eki.ekilex.data;

import java.math.BigDecimal;

import eki.common.data.AbstractDataObject;

public class CollocWeight extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private BigDecimal value;

	private String label;

	public CollocWeight() {
	}

	public CollocWeight(BigDecimal value, String label) {
		this.value = value;
		this.label = label;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
