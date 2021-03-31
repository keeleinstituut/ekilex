package eki.wordweb.data.type;

import eki.common.constant.Complexity;
import eki.common.data.Classifier;
import eki.wordweb.data.ComplexityType;
import eki.wordweb.data.DecoratedWordType;
import eki.wordweb.data.LangType;
import eki.wordweb.data.WordTypeData;

public class TypeLexemeRelation extends WordTypeData implements ComplexityType, LangType, DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Complexity complexity;

	private String lexRelTypeCode;

	private Classifier lexRelType;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	@Override
	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
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
