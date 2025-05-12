package eki.ekilex.data;

import eki.common.constant.Complexity;

public class TypeMtDefinition extends AbstractGrantEntity {

	private static final long serialVersionUID = 1L;

	private Long definitionId;

	private String definitionTypeCode;

	private String value;

	private String valuePrese;

	private String lang;

	private Complexity complexity;

	private boolean isPublic;

	public Long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}

	public String getDefinitionTypeCode() {
		return definitionTypeCode;
	}

	public void setDefinitionTypeCode(String definitionTypeCode) {
		this.definitionTypeCode = definitionTypeCode;
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

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

}
