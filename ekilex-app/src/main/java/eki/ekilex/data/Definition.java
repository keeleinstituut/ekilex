package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

import javax.persistence.Column;

public class Definition extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	@Column(name = "order_by")
	private Long orderBy;

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

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}
}
