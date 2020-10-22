package eki.wordweb.data;

import java.util.List;

import eki.common.data.Classifier;

public class Word extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private String alternativeWord;

	private String wordClass;

	private String displayMorphCode;

	private Classifier displayMorph;

	private List<TypeMeaningWord> meaningWords;

	private String meaningWordsWrapup;

	private List<TypeDefinition> definitions;

	private String definitionsWrapup;

	private List<Classifier> summarisedPoses;

	@Deprecated
	private WordEtymology wordEtymology;

	private WordEtymLevel wordEtymologyTree;

	private List<TypeSourceLink> wordEtymSourceLinks;

	private List<TypeWordRelation> relatedWords;

	private List<WordRelationGroup> primaryRelatedWordTypeGroups;

	private List<WordRelationGroup> secondaryRelatedWordTypeGroups;

	private List<WordGroup> wordGroups;

	private List<String> odWordRecommendations;

	private boolean formsExist;

	private boolean singlePos;

	private boolean wordRelationsExist;

	private boolean selected;

	public String getAlternativeWord() {
		return alternativeWord;
	}

	public void setAlternativeWord(String alternativeWord) {
		this.alternativeWord = alternativeWord;
	}

	public String getWordClass() {
		return wordClass;
	}

	public void setWordClass(String wordClass) {
		this.wordClass = wordClass;
	}

	public String getDisplayMorphCode() {
		return displayMorphCode;
	}

	public void setDisplayMorphCode(String displayMorphCode) {
		this.displayMorphCode = displayMorphCode;
	}

	public Classifier getDisplayMorph() {
		return displayMorph;
	}

	public void setDisplayMorph(Classifier displayMorph) {
		this.displayMorph = displayMorph;
	}

	public List<TypeMeaningWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<TypeMeaningWord> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public String getMeaningWordsWrapup() {
		return meaningWordsWrapup;
	}

	public void setMeaningWordsWrapup(String meaningWordsWrapup) {
		this.meaningWordsWrapup = meaningWordsWrapup;
	}

	public List<TypeDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<TypeDefinition> definitions) {
		this.definitions = definitions;
	}

	public String getDefinitionsWrapup() {
		return definitionsWrapup;
	}

	public void setDefinitionsWrapup(String definitionsWrapup) {
		this.definitionsWrapup = definitionsWrapup;
	}

	public List<Classifier> getSummarisedPoses() {
		return summarisedPoses;
	}

	public void setSummarisedPoses(List<Classifier> summarisedPoses) {
		this.summarisedPoses = summarisedPoses;
	}

	public WordEtymology getWordEtymology() {
		return wordEtymology;
	}

	public void setWordEtymology(WordEtymology wordEtymology) {
		this.wordEtymology = wordEtymology;
	}

	public WordEtymLevel getWordEtymologyTree() {
		return wordEtymologyTree;
	}

	public void setWordEtymologyTree(WordEtymLevel wordEtymologyTree) {
		this.wordEtymologyTree = wordEtymologyTree;
	}

	public List<TypeSourceLink> getWordEtymSourceLinks() {
		return wordEtymSourceLinks;
	}

	public void setWordEtymSourceLinks(List<TypeSourceLink> wordEtymSourceLinks) {
		this.wordEtymSourceLinks = wordEtymSourceLinks;
	}

	public List<TypeWordRelation> getRelatedWords() {
		return relatedWords;
	}

	public void setRelatedWords(List<TypeWordRelation> relatedWords) {
		this.relatedWords = relatedWords;
	}

	public List<WordRelationGroup> getPrimaryRelatedWordTypeGroups() {
		return primaryRelatedWordTypeGroups;
	}

	public void setPrimaryRelatedWordTypeGroups(List<WordRelationGroup> primaryRelatedWordTypeGroups) {
		this.primaryRelatedWordTypeGroups = primaryRelatedWordTypeGroups;
	}

	public List<WordRelationGroup> getSecondaryRelatedWordTypeGroups() {
		return secondaryRelatedWordTypeGroups;
	}

	public void setSecondaryRelatedWordTypeGroups(List<WordRelationGroup> secondaryRelatedWordTypeGroups) {
		this.secondaryRelatedWordTypeGroups = secondaryRelatedWordTypeGroups;
	}

	public List<WordGroup> getWordGroups() {
		return wordGroups;
	}

	public void setWordGroups(List<WordGroup> wordGroups) {
		this.wordGroups = wordGroups;
	}

	public List<String> getOdWordRecommendations() {
		return odWordRecommendations;
	}

	public void setOdWordRecommendations(List<String> odWordRecommendations) {
		this.odWordRecommendations = odWordRecommendations;
	}

	public boolean isFormsExist() {
		return formsExist;
	}

	public void setFormsExist(boolean formsExist) {
		this.formsExist = formsExist;
	}

	public boolean isSinglePos() {
		return singlePos;
	}

	public void setSinglePos(boolean singlePos) {
		this.singlePos = singlePos;
	}

	public boolean isWordRelationsExist() {
		return wordRelationsExist;
	}

	public void setWordRelationsExist(boolean wordRelationsExist) {
		this.wordRelationsExist = wordRelationsExist;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
