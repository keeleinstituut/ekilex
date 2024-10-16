package eki.ekilex.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import eki.common.data.AbstractDataObject;

@JsonIgnoreProperties("valueMatch")
public class SourceProperty extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceId;

	private Long id;

	private String typeCode;

	private String value;

	private boolean valueMatch;

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isValueMatch() {
		return valueMatch;
	}

	public void setValueMatch(boolean valueMatch) {
		this.valueMatch = valueMatch;
	}

}
