package eki.wordweb.data.type;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.data.AbstractDataObject;
import eki.wordweb.data.ComplexityType;
import eki.wordweb.data.SourceLinkType;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TypeFreeform extends AbstractDataObject implements ComplexityType, SourceLinkType {

	private static final long serialVersionUID = 1L;

	private Long freeformId;

	private FreeformType type;

	private String value;

	private String valueCut;

	private String lang;

	private Complexity complexity;

	private String createdBy;

	private Timestamp createdOn;

	private String modifiedBy;

	private Timestamp modifiedOn;

	private List<TypeSourceLink> sourceLinks;

	@Override
	public Long getOwnerId() {
		return freeformId;
	}

	public Long getFreeformId() {
		return freeformId;
	}

	public void setFreeformId(Long freeformId) {
		this.freeformId = freeformId;
	}

	public FreeformType getType() {
		return type;
	}

	public void setType(FreeformType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueCut() {
		return valueCut;
	}

	public void setValueCut(String valueCut) {
		this.valueCut = valueCut;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
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

	@Override
	public List<TypeSourceLink> getSourceLinks() {
		return sourceLinks;
	}

	@Override
	public void setSourceLinks(List<TypeSourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
