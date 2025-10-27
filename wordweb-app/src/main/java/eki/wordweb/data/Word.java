package eki.wordweb.data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.data.Classifier;
import eki.common.util.LocalDateTimeDeserialiser;

public class Word extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private String vocalForm;

	private String morphComment;

	private String alternativeWord;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime manualEventOn;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime lastActivityEventOn;

	private List<MeaningWord> meaningWords;

	private String meaningWordsWrapup;

	private List<Definition> definitions;

	private String definitionsWrapup;

	private List<Classifier> summarisedPoses;

	private String summarisedPosCodesStr;

	private WordEtymLevel wordEtymologyTree;

	private List<WordRelation> relatedWords;

	private List<WordRelationGroup> primaryRelatedWordTypeGroups;

	private List<WordRelationGroup> secondaryRelatedWordTypeGroups;

	private List<WordGroup> wordGroups;

	private List<WordEkiRecommendation> wordEkiRecommendations;

	private boolean wordMatch;

	private boolean formMatch;

	private boolean formsExist;

	private boolean wordRelationsExist;

	private boolean selected;

	private String lexSearchUrl;

	public String getVocalForm() {
		return vocalForm;
	}

	public void setVocalForm(String vocalForm) {
		this.vocalForm = vocalForm;
	}

	public String getMorphComment() {
		return morphComment;
	}

	public void setMorphComment(String morphComment) {
		this.morphComment = morphComment;
	}

	public String getAlternativeWord() {
		return alternativeWord;
	}

	public void setAlternativeWord(String alternativeWord) {
		this.alternativeWord = alternativeWord;
	}

	public LocalDateTime getManualEventOn() {
		return manualEventOn;
	}

	public void setManualEventOn(LocalDateTime manualEventOn) {
		this.manualEventOn = manualEventOn;
	}

	public LocalDateTime getLastActivityEventOn() {
		return lastActivityEventOn;
	}

	public void setLastActivityEventOn(LocalDateTime lastActivityEventOn) {
		this.lastActivityEventOn = lastActivityEventOn;
	}

	public List<MeaningWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<MeaningWord> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public String getMeaningWordsWrapup() {
		return meaningWordsWrapup;
	}

	public void setMeaningWordsWrapup(String meaningWordsWrapup) {
		this.meaningWordsWrapup = meaningWordsWrapup;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
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

	public String getSummarisedPosCodesStr() {
		return summarisedPosCodesStr;
	}

	public void setSummarisedPosCodesStr(String summarisedPosCodesStr) {
		this.summarisedPosCodesStr = summarisedPosCodesStr;
	}

	public WordEtymLevel getWordEtymologyTree() {
		return wordEtymologyTree;
	}

	public void setWordEtymologyTree(WordEtymLevel wordEtymologyTree) {
		this.wordEtymologyTree = wordEtymologyTree;
	}

	public List<WordRelation> getRelatedWords() {
		return relatedWords;
	}

	public void setRelatedWords(List<WordRelation> relatedWords) {
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

	public List<WordEkiRecommendation> getWordEkiRecommendations() {
		return wordEkiRecommendations;
	}

	public void setWordEkiRecommendations(List<WordEkiRecommendation> wordEkiRecommendations) {
		this.wordEkiRecommendations = wordEkiRecommendations;
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

	public String getLexSearchUrl() {
		return lexSearchUrl;
	}

	public void setLexSearchUrl(String lexSearchUrl) {
		this.lexSearchUrl = lexSearchUrl;
	}

}
