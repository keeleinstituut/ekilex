package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class DeleteItemRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String opName;

	private String opCode;

	private Long id;

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

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

}
