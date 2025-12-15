package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class CollocConjunct extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private String label;

	public CollocConjunct() {
	}

	public CollocConjunct(Long lexemeId, String label) {
		super();
		this.lexemeId = lexemeId;
		this.label = label;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
