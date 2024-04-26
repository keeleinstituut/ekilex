package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class LexemeTag extends AbstractDataObject {

	private static final long serialVersionUID = -3625677265675429566L;

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
