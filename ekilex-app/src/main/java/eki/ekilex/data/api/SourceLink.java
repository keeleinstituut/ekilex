package eki.ekilex.data.api;

import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class SourceLink extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceLinkId;

	private String sourceLinkName;

	private Long sourceId;

	private String sourceName;

	private ReferenceType type;

	public Long getSourceLinkId() {
		return sourceLinkId;
	}

	public void setSourceLinkId(Long sourceLinkId) {
		this.sourceLinkId = sourceLinkId;
	}

	public String getSourceLinkName() {
		return sourceLinkName;
	}

	public void setSourceLinkName(String sourceLinkName) {
		this.sourceLinkName = sourceLinkName;
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

	public ReferenceType getType() {
		return type;
	}

	public void setType(ReferenceType type) {
		this.type = type;
	}
}
