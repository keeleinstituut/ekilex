package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class UpdateLexemeLevelsRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Integer position;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
}
