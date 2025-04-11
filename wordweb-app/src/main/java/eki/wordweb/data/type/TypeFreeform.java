package eki.wordweb.data.type;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;
import eki.common.util.LocalDateTimeDeserialiser;
import eki.wordweb.data.ComplexityType;
import eki.wordweb.data.LangType;
import eki.wordweb.data.SourceLinkType;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TypeFreeform extends AbstractDataObject implements ComplexityType, SourceLinkType, LangType {

	private static final long serialVersionUID = 1L;

	private Long freeformId;

	private String freeformTypeCode;

	private String value;

	private String valueCut;

	private String lang;

	private Complexity complexity;

	private String createdBy;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime createdOn;

	private String modifiedBy;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime modifiedOn;

	private List<TypeSourceLink> sourceLinks;

	public Long getFreeformId() {
		return freeformId;
	}

	public void setFreeformId(Long freeformId) {
		this.freeformId = freeformId;
	}

	public String getFreeformTypeCode() {
		return freeformTypeCode;
	}

	public void setFreeformTypeCode(String freeformTypeCode) {
		this.freeformTypeCode = freeformTypeCode;
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

	@Override
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

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public LocalDateTime getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(LocalDateTime modifiedOn) {
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
