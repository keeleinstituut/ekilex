package eki.ekilex.data.api;

import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleEntity;
import eki.common.data.AbstractDataObject;

public class FreeformOwner extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private LifecycleEntity entity;

	private Long entityId;

	private FreeformType type;

	public LifecycleEntity getEntity() {
		return entity;
	}

	public void setEntity(LifecycleEntity entity) {
		this.entity = entity;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public FreeformType getType() {
		return type;
	}

	public void setType(FreeformType type) {
		this.type = type;
	}

}
