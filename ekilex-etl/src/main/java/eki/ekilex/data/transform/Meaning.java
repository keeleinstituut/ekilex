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

	private String processStateCode;

	private String meaningStateCode;

	private String meaningTypeCode;

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

	public String getProcessStateCode() {
		return processStateCode;
	}

	public void setProcessStateCode(String processStateCode) {
		this.processStateCode = processStateCode;
	}

	public String getMeaningStateCode() {
		return meaningStateCode;
	}

	public void setMeaningStateCode(String meaningStateCode) {
		this.meaningStateCode = meaningStateCode;
	}

	public String getMeaningTypeCode() {
		return meaningTypeCode;
	}

	public void setMeaningTypeCode(String meaningTypeCode) {
		this.meaningTypeCode = meaningTypeCode;
	}

}
