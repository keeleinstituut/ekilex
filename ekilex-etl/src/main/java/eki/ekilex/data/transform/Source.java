package eki.ekilex.data.transform;

import eki.common.constant.SourceType;
import eki.common.data.AbstractDataObject;

public class Source extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceId;

	private SourceType type;

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public SourceType getType() {
		return type;
	}

	public void setType(SourceType type) {
		this.type = type;
	}

}
