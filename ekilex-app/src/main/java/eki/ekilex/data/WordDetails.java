package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<Classifier> wordTypes;

	private List<Paradigm> paradigms;

	private List<WordLexeme> lexemes;

	private List<Relation> wordRelations;

	private List<SynRelation> wordSynRelations;

	private List<WordEtym> wordEtymology;

	private List<WordGroup> wordGroups;

	private List<FreeForm> odWordRecommendations;

	private String firstDefinitionValue;

	private Integer wordProcessLogCount;

	private Timestamp lastChangedOn;

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

	public List<FreeForm> getOdWordRecommendations() {
		return odWordRecommendations;
	}

	public void setOdWordRecommendations(List<FreeForm> odWordRecommendations) {
		this.odWordRecommendations = odWordRecommendations;
	}

	public List<SynRelation> getWordSynRelations() {
		return wordSynRelations;
	}

	public void setWordSynRelations(List<SynRelation> wordSynRelations) {
		this.wordSynRelations = wordSynRelations;
	}

	public String getFirstDefinitionValue() {
		return firstDefinitionValue;
	}

	public void setFirstDefinitionValue(String firstDefinitionValue) {
		this.firstDefinitionValue = firstDefinitionValue;
	}

	public Integer getWordProcessLogCount() {
		return wordProcessLogCount;
	}

	public void setWordProcessLogCount(Integer wordProcessLogCount) {
		this.wordProcessLogCount = wordProcessLogCount;
	}

	public Timestamp getLastChangedOn() {
		return lastChangedOn;
	}

	public void setLastChangedOn(Timestamp lastChangedOn) {
		this.lastChangedOn = lastChangedOn;
	}
}
