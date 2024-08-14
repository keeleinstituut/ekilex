package eki.ekilex.data;

import java.util.List;

import eki.common.constant.Complexity;

public class Definition extends AbstractPublicEntity {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String valuePrese;

	private String lang;

	private Complexity complexity;

	private Long orderBy;

	private String typeCode;

	private String typeValue;

	private List<String> datasetCodes;

	private List<DefinitionNote> notes;

	private List<SourceLink> sourceLinks;

	private boolean isEditDisabled;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

	public List<DefinitionNote> getNotes() {
		return notes;
	}

	public void setNotes(List<DefinitionNote> notes) {
		this.notes = notes;
	}

	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

	public boolean isEditDisabled() {
		return isEditDisabled;
	}

	public void setEditDisabled(boolean editDisabled) {
		isEditDisabled = editDisabled;
	}
}
