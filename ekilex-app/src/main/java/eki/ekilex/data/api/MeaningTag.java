package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class MeaningTag extends AbstractDataObject {

	private static final long serialVersionUID = -3625677265675429566L;

	private Long meaningId;

	private String tagName;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

}
