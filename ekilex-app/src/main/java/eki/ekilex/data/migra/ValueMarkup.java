package eki.ekilex.data.migra;

import eki.common.data.AbstractDataObject;

public class ValueMarkup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String valuePrese;

	public ValueMarkup() {
	}

	public ValueMarkup(String value, String valuePrese) {
		this.value = value;
		this.valuePrese = valuePrese;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

}
