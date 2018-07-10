package eki.ekilex.data.transform;

import java.sql.Timestamp;

import eki.common.constant.SourceType;
import eki.common.data.AbstractDataObject;

public class Source extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceId;

	private String extSourceId;

	private Timestamp createdOn;

	private String createdBy;

	private Timestamp modifiedOn;

	private String modifiedBy;

	private SourceType type;

	private String processStateCode;

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getExtSourceId() {
		return extSourceId;
	}

	public void setExtSourceId(String extSourceId) {
		this.extSourceId = extSourceId;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public SourceType getType() {
		return type;
	}

	public void setType(SourceType type) {
		this.type = type;
	}

	public String getProcessStateCode() {
		return processStateCode;
	}

	public void setProcessStateCode(String processStateCode) {
		this.processStateCode = processStateCode;
	}

}
