package eki.wordweb.data.od;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class OdMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private OdDefinition definition;

	private List<OdLexemeWord> lexemeWords;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public OdDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(OdDefinition definition) {
		this.definition = definition;
	}

	public List<OdLexemeWord> getLexemeWords() {
		return lexemeWords;
	}

	public void setLexemeWords(List<OdLexemeWord> lexemeWords) {
		this.lexemeWords = lexemeWords;
	}

}
