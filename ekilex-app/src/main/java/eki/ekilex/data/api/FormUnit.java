package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class FormUnit extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String morphCode;

	public FormUnit() {
	}

	public FormUnit(String value, String morphCode) {
		this.value = value;
		this.morphCode = morphCode;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

}
