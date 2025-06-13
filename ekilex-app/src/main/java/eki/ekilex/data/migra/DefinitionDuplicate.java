package eki.ekilex.data.migra;

import eki.common.data.AbstractDataObject;

public class DefinitionDuplicate extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long definitionId;

	private Long meaningId;

	private Long publishingId;

	private String targetName;

	public Long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public Long getPublishingId() {
		return publishingId;
	}

	public void setPublishingId(Long publishingId) {
		this.publishingId = publishingId;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

}
