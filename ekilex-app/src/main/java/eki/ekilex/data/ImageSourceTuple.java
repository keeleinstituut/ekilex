package eki.ekilex.data;

import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class ImageSourceTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long imageFreeformId;

	private String imageFreeformValueText;

	private String titleFreeformValueText;

	private Long sourceLinkId;

	private ReferenceType sourceLinkType;

	private String sourceLinkName;

	private String sourceLinkValue;

	public Long getImageFreeformId() {
		return imageFreeformId;
	}

	public void setImageFreeformId(Long imageFreeformId) {
		this.imageFreeformId = imageFreeformId;
	}

	public String getImageFreeformValueText() {
		return imageFreeformValueText;
	}

	public void setImageFreeformValueText(String imageFreeformValueText) {
		this.imageFreeformValueText = imageFreeformValueText;
	}

	public String getTitleFreeformValueText() {
		return titleFreeformValueText;
	}

	public void setTitleFreeformValueText(String titleFreeformValueText) {
		this.titleFreeformValueText = titleFreeformValueText;
	}

	public Long getSourceLinkId() {
		return sourceLinkId;
	}

	public void setSourceLinkId(Long sourceLinkId) {
		this.sourceLinkId = sourceLinkId;
	}

	public ReferenceType getSourceLinkType() {
		return sourceLinkType;
	}

	public void setSourceLinkType(ReferenceType sourceLinkType) {
		this.sourceLinkType = sourceLinkType;
	}

	public String getSourceLinkName() {
		return sourceLinkName;
	}

	public void setSourceLinkName(String sourceLinkName) {
		this.sourceLinkName = sourceLinkName;
	}

	public String getSourceLinkValue() {
		return sourceLinkValue;
	}

	public void setSourceLinkValue(String sourceLinkValue) {
		this.sourceLinkValue = sourceLinkValue;
	}
}
