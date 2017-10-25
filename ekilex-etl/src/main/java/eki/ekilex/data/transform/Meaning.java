package eki.ekilex.data.transform;

import java.sql.Timestamp;

import eki.common.data.AbstractDataObject;

public class Meaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private Timestamp createdOn;

	private String createdBy;

	private Timestamp modifiedOn;

	private String modifiedBy;

	private String entryClassCode;

	private String meaningStateCode;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
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

	public String getMeaningStateCode() {
		return meaningStateCode;
	}

	public void setMeaningStateCode(String meaningStateCode) {
		this.meaningStateCode = meaningStateCode;
	}

}
