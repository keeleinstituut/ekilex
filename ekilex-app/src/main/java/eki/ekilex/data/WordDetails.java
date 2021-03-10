package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<Classifier> wordTypes;

	private List<Paradigm> paradigms;

	private List<WordLexeme> lexemes;

	private List<WordEtym> wordEtymology;

	private List<FreeForm> odWordRecommendations;

	private WordRelationDetails wordRelationDetails;

	private String firstDefinitionValue;

	private boolean activeTagComplete;

	public WordDetails() {
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public List<Classifier> getWordTypes() {
		return wordTypes;
	}

	public void setWordTypes(List<Classifier> wordTypes) {
		this.wordTypes = wordTypes;
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

	public List<WordEtym> getWordEtymology() {
		return wordEtymology;
	}

	public void setWordEtymology(List<WordEtym> wordEtymology) {
		this.wordEtymology = wordEtymology;
	}

	public List<FreeForm> getOdWordRecommendations() {
		return odWordRecommendations;
	}

	public void setOdWordRecommendations(List<FreeForm> odWordRecommendations) {
		this.odWordRecommendations = odWordRecommendations;
	}

	public WordRelationDetails getWordRelationDetails() {
		return wordRelationDetails;
	}

	public void setWordRelationDetails(WordRelationDetails wordRelationDetails) {
		this.wordRelationDetails = wordRelationDetails;
	}

	public String getFirstDefinitionValue() {
		return firstDefinitionValue;
	}

	public void setFirstDefinitionValue(String firstDefinitionValue) {
		this.firstDefinitionValue = firstDefinitionValue;
	}

	public boolean isActiveTagComplete() {
		return activeTagComplete;
	}

	public void setActiveTagComplete(boolean activeTagComplete) {
		this.activeTagComplete = activeTagComplete;
	}
}
