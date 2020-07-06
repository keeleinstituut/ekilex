package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class LexemeTag extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private String tagName;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
