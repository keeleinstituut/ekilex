package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class CompData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	public static final int OP_ADD = 1;

	public static final int OP_DEL = 2;

	private int opType;

	private String opValue;

	private CompData(int opType, String opValue) {
		this.opType = opType;
		this.opValue = opValue;
	}

	public static CompData getValueAdded(String value) {
		return new CompData(OP_ADD, value);
	}

	public static CompData getValueDeleted(String value) {
		return new CompData(OP_DEL, value);
	}

	public boolean isValueAdded() {
		return opType == OP_ADD;
	}

	public boolean isValueDeleted() {
		return opType == OP_DEL;
	}

	public String getOpValue() {
		return opValue;
	}
}
