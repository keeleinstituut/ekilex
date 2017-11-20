package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Rection extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String type;

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
}
