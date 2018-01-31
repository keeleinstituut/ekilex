package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Government extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String type;

	private String variant;

	private String optional;

	private List<UsageMeaning> usageMeanings;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<UsageMeaning> getUsageMeanings() {
		return usageMeanings;
	}

	public void setUsageMeanings(List<UsageMeaning> usageMeanings) {
		this.usageMeanings = usageMeanings;
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
