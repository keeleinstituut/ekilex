package eki.ekilex.data;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class ComplexitySelect extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Complexity value;

	private boolean disabled;

	public ComplexitySelect(Complexity value, boolean disabled) {
		this.value = value;
		this.disabled = disabled;
	}

	public Complexity getValue() {
		return value;
	}

	public void setValue(Complexity value) {
		this.value = value;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
