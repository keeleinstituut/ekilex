package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class WordOrForm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String group;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}
