package eki.ekilex.data;

import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class Ref extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String minorRef;

	private String majorRef;

	private ReferenceType type;

	public String getMinorRef() {
		return minorRef;
	}

	public void setMinorRef(String minorRef) {
		this.minorRef = minorRef;
	}

	public String getMajorRef() {
		return majorRef;
	}

	public void setMajorRef(String majorRef) {
		this.majorRef = majorRef;
	}

	public ReferenceType getType() {
		return type;
	}

	public void setType(ReferenceType type) {
		this.type = type;
	}
}
