package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class RelationParam extends AbstractDataObject {

	private static final long serialVersionUID = -8481017888065593956L;

	private String name;

	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
