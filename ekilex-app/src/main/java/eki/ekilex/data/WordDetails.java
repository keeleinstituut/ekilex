package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Classifier> wordTypes;

	private String wordClass;

	private String wordGenderCode;

	private String wordAspectCode;

	private List<Paradigm> paradigms;

	private List<WordLexeme> lexemes;

	private List<Relation> wordRelations;

	private List<WordEtym> wordEtymology;

	private List<WordGroup> wordGroups;

	public WordDetails() {
	}

	public List<Classifier> getWordTypes() {
		return wordTypes;
	}

	public void setWordTypes(List<Classifier> wordTypes) {
		this.wordTypes = wordTypes;
	}

	public String getWordClass() {
		return wordClass;
	}

	public void setWordClass(String wordClass) {
		this.wordClass = wordClass;
	}

	public String getWordGenderCode() {
		return wordGenderCode;
	}

	public void setWordGenderCode(String wordGenderCode) {
		this.wordGenderCode = wordGenderCode;
	}

	public String getWordAspectCode() {
		return wordAspectCode;
	}

	public void setWordAspectCode(String wordAspectCode) {
		this.wordAspectCode = wordAspectCode;
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

	public List<WordGroup> getWordGroups() {
		return wordGroups;
	}

	public void setWordGroups(List<WordGroup> wordGroups) {
		this.wordGroups = wordGroups;
	}

}
