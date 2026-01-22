package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class LexemeVariant extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long wordId;

	private String wordValue;

	private String wordValuePrese;

	private Integer homonymNr;

	private String lang;

	private Long lexemeId;

	private Long variantLexemeId;

	private String variantTypeCode;

	private String variantTypeValue;

	private Long orderBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public String getWordValuePrese() {
		return wordValuePrese;
	}

	public void setWordValuePrese(String wordValuePrese) {
		this.wordValuePrese = wordValuePrese;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getVariantLexemeId() {
		return variantLexemeId;
	}

	public void setVariantLexemeId(Long variantLexemeId) {
		this.variantLexemeId = variantLexemeId;
	}

	public String getVariantTypeCode() {
		return variantTypeCode;
	}

	public void setVariantTypeCode(String variantTypeCode) {
		this.variantTypeCode = variantTypeCode;
	}

	public String getVariantTypeValue() {
		return variantTypeValue;
	}

	public void setVariantTypeValue(String variantTypeValue) {
		this.variantTypeValue = variantTypeValue;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

}
