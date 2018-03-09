package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

import java.util.List;

import javax.persistence.Column;

public class Definition extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "id")
	private Long id;

	@Column(name = "value")
	private String value;

	@Column(name = "order_by")
	private Long orderBy;

	private List<RefLink> refLinks;

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

	public List<RefLink> getRefLinks() {
		return refLinks;
	}

	public void setRefLinks(List<RefLink> refLinks) {
		this.refLinks = refLinks;
	}

}
