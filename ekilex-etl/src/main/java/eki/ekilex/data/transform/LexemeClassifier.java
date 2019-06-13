package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class LexemeClassifier extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private String code;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
