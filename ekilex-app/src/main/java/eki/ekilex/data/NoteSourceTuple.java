package eki.ekilex.data;

import eki.common.constant.Complexity;
import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class NoteSourceTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long freeformId;

	private String valueText;

	private String valuePrese;

	private Complexity complexity;

	private Long sourceLinkId;

	private ReferenceType sourceLinkType;

	private String sourceLinkName;

	private String sourceLinkValue;

	public Long getFreeformId() {
		return freeformId;
	}

	public void setFreeformId(Long freeformId) {
		this.freeformId = freeformId;
	}

	public String getValueText() {
		return valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
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
