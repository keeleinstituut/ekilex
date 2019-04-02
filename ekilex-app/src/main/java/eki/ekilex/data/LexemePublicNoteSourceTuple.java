package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class LexemePublicNoteSourceTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "freeform_id")
	private Long freeformId;

	@Column(name = "freeform_value_text")
	private String valueText;

	@Column(name = "freeform_value_prese")
	private String valuePrese;

	@Column(name = "source_link_id")
	private Long sourceLinkId;

	@Column(name = "source_link_type")
	private ReferenceType sourceLinkType;

	@Column(name = "source_link_name")
	private String sourceLinkName;

	@Column(name = "source_link_value")
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
