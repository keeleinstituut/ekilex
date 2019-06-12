package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Image extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String fileName;

	private String title;

	private List<SourceLink> sourceLinks;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}
}
