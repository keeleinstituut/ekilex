package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class OrderingData extends AbstractDataObject {

	private Long id;

	private Long orderby;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderby() {
		return orderby;
	}

	public void setOrderby(Long orderby) {
		this.orderby = orderby;
	}
}
