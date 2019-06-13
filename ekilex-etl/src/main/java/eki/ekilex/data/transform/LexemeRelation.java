package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class LexemeRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexeme1Id;

	private Long lexeme2Id;

	private String lexemeRelationTypeCode;

	private Long orderBy;

	public Long getLexeme1Id() {
		return lexeme1Id;
	}

	public void setLexeme1Id(Long lexeme1Id) {
		this.lexeme1Id = lexeme1Id;
	}

	public Long getLexeme2Id() {
		return lexeme2Id;
	}

	public void setLexeme2Id(Long lexeme2Id) {
		this.lexeme2Id = lexeme2Id;
	}

	public String getLexemeRelationTypeCode() {
		return lexemeRelationTypeCode;
	}

	public void setLexemeRelationTypeCode(String lexemeRelationTypeCode) {
		this.lexemeRelationTypeCode = lexemeRelationTypeCode;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

}
