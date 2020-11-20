package eki.common.data.transport;

import eki.common.data.AbstractDataObject;

public class SimpleTableColumn extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String tableName;

	private String columnName;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

}
