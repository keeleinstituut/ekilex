package eki.ekilex.data;

import eki.common.constant.Complexity;

public class Government extends AbstractCreateUpdateEntity {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private Complexity complexity;

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

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

}
