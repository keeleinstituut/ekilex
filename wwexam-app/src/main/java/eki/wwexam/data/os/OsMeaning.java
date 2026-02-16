package eki.wwexam.data.os;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class OsMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private OsDefinition definition;

	private List<OsLexemeWord> lexemeWords;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public OsDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(OsDefinition definition) {
		this.definition = definition;
	}

	public List<OsLexemeWord> getLexemeWords() {
		return lexemeWords;
	}

	public void setLexemeWords(List<OsLexemeWord> lexemeWords) {
		this.lexemeWords = lexemeWords;
	}

}
