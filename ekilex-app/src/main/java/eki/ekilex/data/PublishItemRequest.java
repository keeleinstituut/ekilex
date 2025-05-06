package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class PublishItemRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String targetName;

	private String entityName;

	private Long id;

	private boolean value;

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

}
