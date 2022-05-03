package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class TypeValueNameLang extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long valueId;

	private String value;

	private String name;

	private String lang;

	public Long getValueId() {
		return valueId;
	}

	public void setValueId(Long valueId) {
		this.valueId = valueId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
