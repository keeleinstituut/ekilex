package eki.common.data.transport;

import eki.common.data.AbstractDataObject;

public class TableColumn extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String tableName;

	private String columnName;

	private String dataType;

	private Integer charMaxLength;

	private boolean primaryKey;

	private boolean nullable;

	private boolean defaultExists;

	private String fkTableName;

	private String fkColumnName;

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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getCharMaxLength() {
		return charMaxLength;
	}

	public void setCharMaxLength(Integer charMaxLength) {
		this.charMaxLength = charMaxLength;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isDefaultExists() {
		return defaultExists;
	}

	public void setDefaultExists(boolean defaultExists) {
		this.defaultExists = defaultExists;
	}

	public String getFkTableName() {
		return fkTableName;
	}

	public void setFkTableName(String fkTableName) {
		this.fkTableName = fkTableName;
	}

	public String getFkColumnName() {
		return fkColumnName;
	}

	public void setFkColumnName(String fkColumnName) {
		this.fkColumnName = fkColumnName;
	}

}
