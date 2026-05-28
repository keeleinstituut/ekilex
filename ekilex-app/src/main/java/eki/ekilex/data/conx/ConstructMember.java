package eki.ekilex.data.conx;

import eki.common.data.AbstractDataObject;

public class ConstructMember extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long constructId;

	private String cgovernmentCode;

	private boolean isHead;

	private String memberRole;

	private Integer memberOrder;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getConstructId() {
		return constructId;
	}

	public void setConstructId(Long constructId) {
		this.constructId = constructId;
	}

	public String getCgovernmentCode() {
		return cgovernmentCode;
	}

	public void setCgovernmentCode(String cgovernmentCode) {
		this.cgovernmentCode = cgovernmentCode;
	}

	public boolean isHead() {
		return isHead;
	}

	public void setHead(boolean isHead) {
		this.isHead = isHead;
	}

	public String getMemberRole() {
		return memberRole;
	}

	public void setMemberRole(String memberRole) {
		this.memberRole = memberRole;
	}

	public Integer getMemberOrder() {
		return memberOrder;
	}

	public void setMemberOrder(Integer memberOrder) {
		this.memberOrder = memberOrder;
	}

}
