package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class Form extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String valuePrese;

	private String morphCode;

	public Form() {
	}

	public Form(Long id, String value, String valuePrese, String morphCode) {
		this.id = id;
		this.value = value;
		this.valuePrese = valuePrese;
		this.morphCode = morphCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

}
