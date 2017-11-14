package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.function.Consumer;

import javax.persistence.Column;

import eki.common.constant.FreeformType;
import eki.common.data.AbstractDataObject;

public class SourceHeadingPropertyTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "source_id")
	private Long sourceId;

	@Column(name = "concept")
	private String concept;

	@Column(name = "created_on")
	private Timestamp createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "modified_on")
	private Timestamp modifiedOn;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "entry_class_code")
	private String entryClassCode;

	@Column(name = "type")
	private String type;

	@Column(name = "source_heading_id")
	private Long sourceHeadingId;

	@Column(name = "source_heading_type")
	private FreeformType sourceHeadingType;

	@Column(name = "source_heading_value")
	private String sourceHeadingValue;

	@Column(name = "source_property_id")
	private Long sourcePropertyId;

	@Column(name = "source_property_type")
	private FreeformType sourcePropertyType;

	@Column(name = "source_property_value_text")
	private String sourcePropertyValueText;

	@Column(name = "source_property_value_date")
	private Timestamp sourcePropertyValueDate;

	public SourceHeadingPropertyTuple() {
	}

	public SourceHeadingPropertyTuple(Consumer<SourceHeadingPropertyTuple> builder) {
		builder.accept(this);
	}

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

	public Long getSourceHeadingId() {
		return sourceHeadingId;
	}

	public void setSourceHeadingId(Long sourceHeadingId) {
		this.sourceHeadingId = sourceHeadingId;
	}

	public FreeformType getSourceHeadingType() {
		return sourceHeadingType;
	}

	public void setSourceHeadingType(FreeformType sourceHeadingType) {
		this.sourceHeadingType = sourceHeadingType;
	}

	public String getSourceHeadingValue() {
		return sourceHeadingValue;
	}

	public void setSourceHeadingValue(String sourceHeadingValue) {
		this.sourceHeadingValue = sourceHeadingValue;
	}

	public Long getSourcePropertyId() {
		return sourcePropertyId;
	}

	public void setSourcePropertyId(Long sourcePropertyId) {
		this.sourcePropertyId = sourcePropertyId;
	}

	public FreeformType getSourcePropertyType() {
		return sourcePropertyType;
	}

	public void setSourcePropertyType(FreeformType sourcePropertyType) {
		this.sourcePropertyType = sourcePropertyType;
	}

	public String getSourcePropertyValueText() {
		return sourcePropertyValueText;
	}

	public void setSourcePropertyValueText(String sourcePropertyValueText) {
		this.sourcePropertyValueText = sourcePropertyValueText;
	}

	public Timestamp getSourcePropertyValueDate() {
		return sourcePropertyValueDate;
	}

	public void setSourcePropertyValueDate(Timestamp sourcePropertyValueDate) {
		this.sourcePropertyValueDate = sourcePropertyValueDate;
	}

}
