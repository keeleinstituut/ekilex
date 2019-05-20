package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordDescript extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<WordLexeme> lexemes;

	private List<String> definitions;

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public List<WordLexeme> getLexemes() {
		return lexemes;
	}

	public void setLexemes(List<WordLexeme> lexemes) {
		this.lexemes = lexemes;
	}

	public List<String> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
	}

}
