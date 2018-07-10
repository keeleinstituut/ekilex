package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class Source extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceId;

	private String extSourceId;

	private Timestamp createdOn;

	private String createdBy;

	private Timestamp modifiedOn;

	private String modifiedBy;

	private String processStateCode;

	private String type;

	private List<String> sourceNames;

	private List<SourceProperty> sourceProperties;

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

	public String getProcessStateCode() {
		return processStateCode;
	}

	public void setProcessStateCode(String processStateCode) {
		this.processStateCode = processStateCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getSourceNames() {
		return sourceNames;
	}

	public void setSourceNames(List<String> sourceNames) {
		this.sourceNames = sourceNames;
	}

	public List<SourceProperty> getSourceProperties() {
		return sourceProperties;
	}

	public void setSourceProperties(List<SourceProperty> sourceProperties) {
		this.sourceProperties = sourceProperties;
	}

}
