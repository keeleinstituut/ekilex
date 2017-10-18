package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class Form extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String morphCode;

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
