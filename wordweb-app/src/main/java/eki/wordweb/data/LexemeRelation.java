package eki.wordweb.data;

import eki.common.data.Classifier;

public class LexemeRelation extends WordTypeData implements LangType, DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private String lexRelTypeCode;

	private Classifier lexRelType;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public String getLexRelTypeCode() {
		return lexRelTypeCode;
	}

	public void setLexRelTypeCode(String lexRelTypeCode) {
		this.lexRelTypeCode = lexRelTypeCode;
	}

	public Classifier getLexRelType() {
		return lexRelType;
	}

	public void setLexRelType(Classifier lexRelType) {
		this.lexRelType = lexRelType;
	}

}
