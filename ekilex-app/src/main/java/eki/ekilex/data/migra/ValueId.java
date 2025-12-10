package eki.ekilex.data.migra;

import eki.common.data.AbstractDataObject;

public class ValueId extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String tableName;

	private Long id;

	private String value;

	private String valuePrese;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

}
