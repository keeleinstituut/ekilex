package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TypeImageFile extends AbstractDataObject implements SourceLinkType {

	private static final long serialVersionUID = 1L;

	private Long freeformId;

	private String imageFile;

	private String imageTitle;

	private List<TypeSourceLink> sourceLinks;

	@Override
	public Long getOwnerId() {
		return freeformId;
	}

	public Long getFreeformId() {
		return freeformId;
	}

	public void setFreeformId(Long freeformId) {
		this.freeformId = freeformId;
	}

	public String getImageFile() {
		return imageFile;
	}

	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}

	public String getImageTitle() {
		return imageTitle;
	}

	public void setImageTitle(String imageTitle) {
		this.imageTitle = imageTitle;
	}

	@Override
	public List<TypeSourceLink> getSourceLinks() {
		return sourceLinks;
	}

	@Override
	public void setSourceLinks(List<TypeSourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
