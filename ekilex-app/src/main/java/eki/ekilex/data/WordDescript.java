package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordDescript extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<WordLexeme> lexemes;

	private List<WordLexeme> mainDatasetLexemes;

	private List<WordLexeme> secondaryDatasetLexemes;

	private List<String> definitions;

	private boolean primaryDatasetLexemeExists;

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

	public List<WordLexeme> getMainDatasetLexemes() {
		return mainDatasetLexemes;
	}

	public void setMainDatasetLexemes(List<WordLexeme> mainDatasetLexemes) {
		this.mainDatasetLexemes = mainDatasetLexemes;
	}

	public List<WordLexeme> getSecondaryDatasetLexemes() {
		return secondaryDatasetLexemes;
	}

	public void setSecondaryDatasetLexemes(List<WordLexeme> secondaryDatasetLexemes) {
		this.secondaryDatasetLexemes = secondaryDatasetLexemes;
	}

	public List<String> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
	}

	public boolean isPrimaryDatasetLexemeExists() {
		return primaryDatasetLexemeExists;
	}

	public void setPrimaryDatasetLexemeExists(boolean primaryDatasetLexemeExists) {
		this.primaryDatasetLexemeExists = primaryDatasetLexemeExists;
	}
}
