package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class UiFilterElement extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String code;

	private String value;

	private boolean addSeparator;

	private boolean selected;

	public UiFilterElement() {
	}

	public UiFilterElement(String code, String value, boolean addSeparator) {
		this.code = code;
		this.value = value;
		this.addSeparator = addSeparator;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isAddSeparator() {
		return addSeparator;
	}

	public void setAddSeparator(boolean addSeparator) {
		this.addSeparator = addSeparator;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
