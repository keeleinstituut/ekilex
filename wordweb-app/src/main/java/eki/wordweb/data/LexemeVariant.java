package eki.wordweb.data;

import eki.common.data.Classifier;

public class LexemeVariant extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long variantLexemeId;

	private String variantTypeCode;

	private Classifier variantType;

	private Long orderBy;

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

	public Classifier getVariantType() {
		return variantType;
	}

	public void setVariantType(Classifier variantType) {
		this.variantType = variantType;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

}
