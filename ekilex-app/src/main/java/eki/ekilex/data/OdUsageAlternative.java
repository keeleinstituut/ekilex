package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class OdUsageAlternative extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

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
}
