package eki.wordweb.data.os;

import eki.common.data.AbstractDataObject;

public class WordOsUsage extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordOsUsageId;

	private String value;

	private String valuePrese;

	private Long orderBy;

	public Long getWordOsUsageId() {
		return wordOsUsageId;
	}

	public void setWordOsUsageId(Long wordOsUsageId) {
		this.wordOsUsageId = wordOsUsageId;
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

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

}
