package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

/**
 * Domain code origin and value pojo.
 */

public class CodeOriginTuple extends AbstractDataObject {

	private String code;

	private String origin;

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
