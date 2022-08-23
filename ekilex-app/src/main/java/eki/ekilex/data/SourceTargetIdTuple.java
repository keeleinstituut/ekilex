package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class SourceTargetIdTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceId;

	private Long targetId;

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
}
