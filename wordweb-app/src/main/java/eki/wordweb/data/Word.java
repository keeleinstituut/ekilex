package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class Word extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String word;

	private Integer homonymNr;

	private String lang;

	private String morphCode;

	private String displayMorphCode;

	private String typeCode;

	private Classifier type;

	private String aspectCode;

	private Classifier aspect;

	private List<String> datasetCodes;

	private Integer meaningCount;

	private List<TypeWord> meaningWords;

	private String meaningWordsWrapup;

	private List<TypeDefinition> definitions;

	private String definitionsWrapup;

	private List<TypeWordRelation> relatedWords;

	private List<WordRelationGroup> relatedWordTypeGroups;

	private List<WordGroup> wordGroups;

	private boolean wordRelationsExist;

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

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
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

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public Classifier getType() {
		return type;
	}

	public void setType(Classifier type) {
		this.type = type;
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

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
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

	public List<TypeWordRelation> getRelatedWords() {
		return relatedWords;
	}

	public void setRelatedWords(List<TypeWordRelation> relatedWords) {
		this.relatedWords = relatedWords;
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
