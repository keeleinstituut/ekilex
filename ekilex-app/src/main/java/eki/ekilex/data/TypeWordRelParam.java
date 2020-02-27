package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class TypeWordRelParam extends AbstractDataObject {

	private static final long serialVersionUID = -8481017888065593956L;

	private String name;

	private Float value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}
}
