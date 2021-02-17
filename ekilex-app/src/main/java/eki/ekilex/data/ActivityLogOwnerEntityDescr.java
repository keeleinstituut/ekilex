package eki.ekilex.data;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.data.AbstractDataObject;

public class ActivityLogOwnerEntityDescr extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private ActivityOwner ownerName;

	private Long ownerId;

	private ActivityEntity entityName;

	public ActivityLogOwnerEntityDescr(ActivityOwner ownerName, Long ownerId, ActivityEntity entityName) {
		this.ownerName = ownerName;
		this.ownerId = ownerId;
		this.entityName = entityName;
	}

	public ActivityOwner getOwnerName() {
		return ownerName;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public ActivityEntity getEntityName() {
		return entityName;
	}

}
