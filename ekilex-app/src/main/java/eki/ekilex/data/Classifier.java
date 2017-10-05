package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class Classifier extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "code")
	private String code;

	@Column(name = "origin")
	private String origin;

	@Column(name = "value")
	private String value;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
