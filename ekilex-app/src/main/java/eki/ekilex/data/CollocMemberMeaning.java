package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class CollocMemberMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long lexemeId;

	private Long meaningId;

	private List<String> definitionValues;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public List<String> getDefinitionValues() {
		return definitionValues;
	}

	public void setDefinitionValues(List<String> definitionValues) {
		this.definitionValues = definitionValues;
	}

}
