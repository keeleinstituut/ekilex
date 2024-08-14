package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class SourceLinkOwner extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long ownerId;

	private Long sourceId;

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

}
