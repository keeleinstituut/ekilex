package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class RefLink extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "value")
	private String value;

	@Column(name = "owner_id")
	private Long ownerId;

	@Column(name = "ref_type")
	private ReferenceType refType;

	@Column(name = "ref_id")
	private Long refId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
