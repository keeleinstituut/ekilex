package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class PublishItemRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String opCode;

	private Long id;

	private boolean value;

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

}
