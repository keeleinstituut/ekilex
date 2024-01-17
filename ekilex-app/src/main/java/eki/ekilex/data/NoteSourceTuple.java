package eki.ekilex.data;

import java.sql.Timestamp;

import eki.common.constant.Complexity;
import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class NoteSourceTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long freeformId;

	private String valueText;

	private String valuePrese;

	private String lang;

	private Complexity complexity;

	private boolean isPublic;

	private Long orderBy;

	private String modifiedBy;

	private Timestamp modifiedOn;

	private Long sourceLinkId;

	private ReferenceType sourceLinkType;

	private String sourceLinkName;

	private Long sourceId;

	private String sourceName;

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

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean aPublic) {
		isPublic = aPublic;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
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

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
}
