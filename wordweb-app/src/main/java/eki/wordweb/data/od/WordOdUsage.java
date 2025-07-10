package eki.wordweb.data.od;

import eki.common.data.AbstractDataObject;

public class WordOdUsage extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordOdUsageId;

	private String value;

	private String valuePrese;

	private Long orderBy;

	public Long getWordOdUsageId() {
		return wordOdUsageId;
	}

	public void setWordOdUsageId(Long wordOdUsageId) {
		this.wordOdUsageId = wordOdUsageId;
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
