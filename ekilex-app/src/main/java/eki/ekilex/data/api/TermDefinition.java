package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class TermDefinition extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long definitionId;

	private String value;

	private String lang;

	private String definitionTypeCode;

	public Long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getDefinitionTypeCode() {
		return definitionTypeCode;
	}

	public void setDefinitionTypeCode(String definitionTypeCode) {
		this.definitionTypeCode = definitionTypeCode;
	}

}
