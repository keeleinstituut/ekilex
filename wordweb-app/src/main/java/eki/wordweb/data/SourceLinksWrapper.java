package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class SourceLinksWrapper extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<TypeSourceLink> sourceLinks;

	public List<TypeSourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<TypeSourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
