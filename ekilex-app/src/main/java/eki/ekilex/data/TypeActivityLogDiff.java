package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class TypeActivityLogDiff extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String op;

	private String path;

	private String value;

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
