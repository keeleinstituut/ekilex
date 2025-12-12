package eki.ekilex.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class Definition extends AbstractGrantEntity {

	private static final long serialVersionUID = 1L;
	@Schema(example = "43367")
	private Long id;
	@Schema(example = "samal varrel või selle eraldi raokestel kasvavate viljade või õite tihe kogum")
	private String value;
	@Schema(example = "samal varrel või selle eraldi raokestel kasvavate viljade või õite tihe kogum")
	private String valuePrese;
	@Schema(example = "est")
	private String lang;
	@Schema(example = "33368")
	private Long orderBy;
	@Schema(example = "määramata")
	private String typeCode;
	@Schema(example = "-")
	private String typeValue;
	@Schema(example = "[\n" +
			"\"eki\"\n" +
			"]")
	private List<String> datasetCodes;
	@Schema(example = "null")
	private List<DefinitionNote> notes;
	@Schema(example = "null")
	private List<SourceLink> sourceLinks;
	@Schema(example = "false")
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
