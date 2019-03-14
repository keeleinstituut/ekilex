package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class StatDataRow extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String name;

	private long rowCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}
}
