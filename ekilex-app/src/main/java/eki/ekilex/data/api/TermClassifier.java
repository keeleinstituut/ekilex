package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class TermClassifier extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String code;

	private String origin;

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
}
