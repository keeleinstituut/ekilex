package eki.ekilex.data;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.data.AbstractDataObject;

public class ActivityContext extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long ownerId;

	private ActivityOwner ownerName;

	private Long entityId;

	private ActivityEntity entityName;

	public ActivityContext(Long ownerId, ActivityOwner ownerName, Long entityId, ActivityEntity entityName) {
		this.ownerId = ownerId;
		this.ownerName = ownerName;
		this.entityId = entityId;
		this.entityName = entityName;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public ActivityOwner getOwnerName() {
		return ownerName;
	}

	public Long getEntityId() {
		return entityId;
	}

	public ActivityEntity getEntityName() {
		return entityName;
	}

}
