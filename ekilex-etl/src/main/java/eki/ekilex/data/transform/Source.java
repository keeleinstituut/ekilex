package eki.ekilex.data.transform;

import java.sql.Timestamp;

import eki.common.data.AbstractDataObject;

public class Source extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceId;

	private String concept;

	private Timestamp createdOn;

	private String createdBy;

	private Timestamp modifiedOn;

	private String modifiedBy;

	private String entryClassCode;

	private String type;

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
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

	public String getEntryClassCode() {
		return entryClassCode;
	}

	public void setEntryClassCode(String entryClassCode) {
		this.entryClassCode = entryClassCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
