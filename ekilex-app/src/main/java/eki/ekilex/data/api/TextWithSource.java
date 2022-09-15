package eki.ekilex.data.api;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TextWithSource extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private List<SourceLink> sourceLinks;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
