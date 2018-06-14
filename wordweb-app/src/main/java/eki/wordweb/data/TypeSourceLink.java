package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class TypeSourceLink extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceLinkId;

	private Long sourceId;

	private String sourceLinkType;

	private String sourceName;

	public Long getSourceLinkId() {
		return sourceLinkId;
	}

	public void setSourceLinkId(Long sourceLinkId) {
		this.sourceLinkId = sourceLinkId;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceLinkType() {
		return sourceLinkType;
	}

	public void setSourceLinkType(String sourceLinkType) {
		this.sourceLinkType = sourceLinkType;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

}
