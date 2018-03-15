package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class TypeDomain extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String origin;

	private String code;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
