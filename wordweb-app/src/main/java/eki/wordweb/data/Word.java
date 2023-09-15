package eki.wordweb.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.data.Classifier;
import eki.wordweb.data.type.TypeDefinition;
import eki.wordweb.data.type.TypeFreeform;
import eki.wordweb.data.type.TypeMeaningWord;
import eki.wordweb.data.type.TypeSourceLink;
import eki.wordweb.data.type.TypeWordRelation;

public class Word extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private String vocalForm;

	private String alternativeWord;

	private Timestamp manualEventOn;

	private Timestamp lastActivityEventOn;

	private List<TypeMeaningWord> meaningWords;

	private String meaningWordsWrapup;

	private List<TypeDefinition> definitions;

	private String definitionsWrapup;

	private List<Classifier> summarisedPoses;

	private WordEtymLevel wordEtymologyTree;

	private List<TypeSourceLink> wordEtymSourceLinks;

	private List<TypeWordRelation> relatedWords;

	private List<WordRelationGroup> primaryRelatedWordTypeGroups;

	private List<WordRelationGroup> secondaryRelatedWordTypeGroups;

	private List<WordGroup> wordGroups;

	private List<TypeFreeform> odWordRecommendations;

	private boolean wordMatch;

	private boolean formMatch;

	private boolean formsExist;

	private boolean wordRelationsExist;

	private boolean selected;

	public String getVocalForm() {
		return vocalForm;
	}

	public void setVocalForm(String vocalForm) {
		this.vocalForm = vocalForm;
	}

	public String getAlternativeWord() {
		return alternativeWord;
	}

	public void setAlternativeWord(String alternativeWord) {
		this.alternativeWord = alternativeWord;
	}

	public Timestamp getManualEventOn() {
		return manualEventOn;
	}

	public void setManualEventOn(Timestamp manualEventOn) {
		this.manualEventOn = manualEventOn;
	}

	public Timestamp getLastActivityEventOn() {
		return lastActivityEventOn;
	}

	public void setLastActivityEventOn(Timestamp lastActivityEventOn) {
		this.lastActivityEventOn = lastActivityEventOn;
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

	public List<TypeFreeform> getOdWordRecommendations() {
		return odWordRecommendations;
	}

	public void setOdWordRecommendations(List<TypeFreeform> odWordRecommendations) {
		this.odWordRecommendations = odWordRecommendations;
	}

	public boolean isWordMatch() {
		return wordMatch;
	}

	public void setWordMatch(boolean wordMatch) {
		this.wordMatch = wordMatch;
	}

	public boolean isFormMatch() {
		return formMatch;
	}

	public void setFormMatch(boolean formMatch) {
		this.formMatch = formMatch;
	}

	public boolean isFormsExist() {
		return formsExist;
	}

	public void setFormsExist(boolean formsExist) {
		this.formsExist = formsExist;
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
