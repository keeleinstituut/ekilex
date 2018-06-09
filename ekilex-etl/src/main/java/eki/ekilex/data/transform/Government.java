package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class Government extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String type;

	private String variant;

	private String optional;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getOptional() {
		return optional;
	}

	public void setOptional(String optional) {
		this.optional = optional;
	}
}
