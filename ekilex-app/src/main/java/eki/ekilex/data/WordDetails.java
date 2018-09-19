package eki.ekilex.data;

import java.util.List;
import java.util.function.Consumer;

import eki.common.data.AbstractDataObject;

public class WordDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Paradigm> paradigms;

	private List<WordLexeme> lexemes;

	private List<Relation> wordRelations;

	private List<WordEtym> wordEtymology;

	public WordDetails() {
	}

	public WordDetails(Consumer<WordDetails> builder) {
		builder.accept(this);
	}

	public List<Paradigm> getParadigms() {
		return paradigms;
	}

	public void setParadigms(List<Paradigm> paradigms) {
		this.paradigms = paradigms;
	}

	public List<WordLexeme> getLexemes() {
		return lexemes;
	}

	public void setLexemes(List<WordLexeme> lexemes) {
		this.lexemes = lexemes;
	}

	public List<Relation> getWordRelations() {
		return wordRelations;
	}

	public void setWordRelations(List<Relation> wordRelations) {
		this.wordRelations = wordRelations;
	}

	public List<WordEtym> getWordEtymology() {
		return wordEtymology;
	}

	public void setWordEtymology(List<WordEtym> wordEtymology) {
		this.wordEtymology = wordEtymology;
	}

}
