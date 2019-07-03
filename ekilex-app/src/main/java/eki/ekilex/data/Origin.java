package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class Origin extends AbstractDataObject {

	private static final long serialVersionUID = 6530661418154816503L;

	private String code;

	private String label;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
