package eki.ekilex.data;

import java.util.function.Consumer;

import javax.persistence.Column;

import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class RefLink extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "owner_id")
	private Long ownerId;

	@Column(name = "ref_type")
	private ReferenceType refType;

	@Column(name = "ref_id")
	private Long refId;

	public RefLink() {
	}

	public RefLink(Consumer<RefLink> builder) {
		builder.accept(this);
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public ReferenceType getRefType() {
		return refType;
	}

	public void setRefType(ReferenceType refType) {
		this.refType = refType;
	}

	public Long getRefId() {
		return refId;
	}

	public void setRefId(Long refId) {
		this.refId = refId;
	}

}
