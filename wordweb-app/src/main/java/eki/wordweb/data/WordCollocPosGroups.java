package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordCollocPosGroups extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long wordId;

	private List<CollocPosGroup> posGroups;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public List<CollocPosGroup> getPosGroups() {
		return posGroups;
	}

	public void setPosGroups(List<CollocPosGroup> posGroups) {
		this.posGroups = posGroups;
	}

}
