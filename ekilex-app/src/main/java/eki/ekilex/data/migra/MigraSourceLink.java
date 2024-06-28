package eki.ekilex.data.migra;

import eki.common.data.AbstractDataObject;

public class MigraSourceLink extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceId;

	private Long sourceLinkId;

	private SourceLinkOwner sourceLinkOwner;

	private Long sourceLinkOwnerId;

	private String datasetCode;

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Long getSourceLinkId() {
		return sourceLinkId;
	}

	public void setSourceLinkId(Long sourceLinkId) {
		this.sourceLinkId = sourceLinkId;
	}

	public SourceLinkOwner getSourceLinkOwner() {
		return sourceLinkOwner;
	}

	public void setSourceLinkOwner(SourceLinkOwner sourceLinkOwner) {
		this.sourceLinkOwner = sourceLinkOwner;
	}

	public Long getSourceLinkOwnerId() {
		return sourceLinkOwnerId;
	}

	public void setSourceLinkOwnerId(Long sourceLinkOwnerId) {
		this.sourceLinkOwnerId = sourceLinkOwnerId;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

}
