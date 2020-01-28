package eki.wordweb.data;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class TypeDefinition extends AbstractDataObject implements ComplexityType, LangType {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private String value;

	private String valuePrese;

	private String lang;

	private Complexity complexity;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
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

	@Override
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

}
