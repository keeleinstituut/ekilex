package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public class LexemeClassifier extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private String classifierCode;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public String getClassifierCode() {
		return classifierCode;
	}

	public void setClassifierCode(String classifierCode) {
		this.classifierCode = classifierCode;
	}
}
