package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class ListData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String code;

	private Long orderby;

	private boolean selected;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getOrderby() {
		return orderby;
	}

	public void setOrderby(Long orderby) {
		this.orderby = orderby;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
