package eki.wordweb.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.Classifier;

public class Word extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String word;

	private String wordClass;

	private String lang;

	private Integer homonymNr;

	private String morphCode;

	private String displayMorphCode;

	private Classifier displayMorph;

	private String aspectCode;

	private Classifier aspect;

	private Complexity complexity;

	private Integer meaningCount;

	private List<TypeWord> meaningWords;

	private String meaningWordsWrapup;

	private List<TypeDefinition> definitions;

	private String definitionsWrapup;

	private List<Classifier> summarisedPoses;

	private WordEtymology wordEtymology;

	private List<TypeWordRelation> relatedWords;

	private List<WordRelationGroup> limitedRelatedWordTypeGroups;

	private List<WordRelationGroup> relatedWordTypeGroups;

	private List<WordGroup> wordGroups;

	private boolean singlePos;

	private boolean wordRelationsExist;

	private boolean moreWordRelations;

	private boolean selected;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWordClass() {
		return wordClass;
	}

	public void setWordClass(String wordClass) {
		this.wordClass = wordClass;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
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

	public String getAspectCode() {
		return aspectCode;
	}

	public void setAspectCode(String aspectCode) {
		this.aspectCode = aspectCode;
	}

	public Classifier getAspect() {
		return aspect;
	}

	public void setAspect(Classifier aspect) {
		this.aspect = aspect;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public Integer getMeaningCount() {
		return meaningCount;
	}

	public void setMeaningCount(Integer meaningCount) {
		this.meaningCount = meaningCount;
	}

	public List<TypeWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<TypeWord> meaningWords) {
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

	public List<TypeWordRelation> getRelatedWords() {
		return relatedWords;
	}

	public void setRelatedWords(List<TypeWordRelation> relatedWords) {
		this.relatedWords = relatedWords;
	}

	public List<WordRelationGroup> getLimitedRelatedWordTypeGroups() {
		return limitedRelatedWordTypeGroups;
	}

	public void setLimitedRelatedWordTypeGroups(List<WordRelationGroup> limitedRelatedWordTypeGroups) {
		this.limitedRelatedWordTypeGroups = limitedRelatedWordTypeGroups;
	}

	public List<WordRelationGroup> getRelatedWordTypeGroups() {
		return relatedWordTypeGroups;
	}

	public void setRelatedWordTypeGroups(List<WordRelationGroup> relatedWordTypeGroups) {
		this.relatedWordTypeGroups = relatedWordTypeGroups;
	}

	public List<WordGroup> getWordGroups() {
		return wordGroups;
	}

	public void setWordGroups(List<WordGroup> wordGroups) {
		this.wordGroups = wordGroups;
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

	public boolean isMoreWordRelations() {
		return moreWordRelations;
	}

	public void setMoreWordRelations(boolean moreWordRelations) {
		this.moreWordRelations = moreWordRelations;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
