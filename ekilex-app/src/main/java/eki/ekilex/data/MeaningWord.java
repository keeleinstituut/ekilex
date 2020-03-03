package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class MeaningWord extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String value;

	private String valuePrese;

	private Integer homonymNumber;

	private String language;

	private Long lexemeId;

	private String lexemeType;

	private Float lexemeWeight;

	private Long orderBy;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
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

	public Integer getHomonymNumber() {
		return homonymNumber;
	}

	public void setHomonymNumber(Integer homonymNumber) {
		this.homonymNumber = homonymNumber;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public String getLexemeType() {
		return lexemeType;
	}

	public void setLexemeType(String lexemeType) {
		this.lexemeType = lexemeType;
	}

	public Float getLexemeWeight() {
		return lexemeWeight;
	}

	public void setLexemeWeight(Float lexemeWeight) {
		this.lexemeWeight = lexemeWeight;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}
}
