package eki.ekilex.data;

import eki.common.constant.ActivityEntity;
import eki.common.constant.LifecycleLogOwner;
import eki.common.data.AbstractDataObject;

public class ActivityLogOwnerEntityDescr extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private LifecycleLogOwner ownerName;

	private Long ownerId;

	private ActivityEntity entityName;

	public ActivityLogOwnerEntityDescr(LifecycleLogOwner ownerName, Long ownerId, ActivityEntity entityName) {
		this.ownerName = ownerName;
		this.ownerId = ownerId;
		this.entityName = entityName;
	}

	public LifecycleLogOwner getOwnerName() {
		return ownerName;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public ActivityEntity getEntityName() {
		return entityName;
	}

}
