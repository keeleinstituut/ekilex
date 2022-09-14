package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class MeaningTableRow extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private List<TypeMtDefinition> definitions;

	private List<TypeMtLexeme> lexemes;

	private List<TypeMtWord> words;

	private List<TypeMtLexemeFreeform> usages;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public List<TypeMtDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<TypeMtDefinition> definitions) {
		this.definitions = definitions;
	}

	public List<TypeMtLexeme> getLexemes() {
		return lexemes;
	}

	public void setLexemes(List<TypeMtLexeme> lexemes) {
		this.lexemes = lexemes;
	}

	public List<TypeMtWord> getWords() {
		return words;
	}

	public void setWords(List<TypeMtWord> words) {
		this.words = words;
	}

	public List<TypeMtLexemeFreeform> getUsages() {
		return usages;
	}

	public void setUsages(List<TypeMtLexemeFreeform> usages) {
		this.usages = usages;
	}

}
