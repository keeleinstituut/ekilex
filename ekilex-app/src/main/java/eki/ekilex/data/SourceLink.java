package eki.ekilex.data;

import eki.common.constant.ReferenceOwner;
import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class SourceLink extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	// TODO is this necessary?
	@Deprecated
	private ReferenceOwner owner;

	// TODO is this necessary?
	@Deprecated
	private Long ownerId;

	private String contentKey;

	private Long id;

	private ReferenceType type;

	private String name;

	private Long sourceId;

	private String sourceName;

	public ReferenceOwner getOwner() {
		return owner;
	}

	public void setOwner(ReferenceOwner owner) {
		this.owner = owner;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getContentKey() {
		return contentKey;
	}

	public void setContentKey(String contentKey) {
		this.contentKey = contentKey;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ReferenceType getType() {
		return type;
	}

	public void setType(ReferenceType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
}
