package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class InexactSynMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private String datasetCode;

	private String datasetName;

	private List<Definition> definitions;

	private List<String> meaningWordValues;

	private String translationLangWordValue;

	private List<WordDescript> translationLangWordCandidates;

	private List<WordLexeme> translationLangWords;

	private String targetLangWordValue;

	private List<WordDescript> targetLangWordCandidates;

	private List<WordLexeme> targetLangWords;

	private boolean isInexactSynDefinitionMandatory;

	private boolean isComplete;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	public List<String> getMeaningWordValues() {
		return meaningWordValues;
	}

	public void setMeaningWordValues(List<String> meaningWordValues) {
		this.meaningWordValues = meaningWordValues;
	}

	public String getTranslationLangWordValue() {
		return translationLangWordValue;
	}

	public void setTranslationLangWordValue(String translationLangWordValue) {
		this.translationLangWordValue = translationLangWordValue;
	}

	public List<WordDescript> getTranslationLangWordCandidates() {
		return translationLangWordCandidates;
	}

	public void setTranslationLangWordCandidates(List<WordDescript> translationLangWordCandidates) {
		this.translationLangWordCandidates = translationLangWordCandidates;
	}

	public List<WordLexeme> getTranslationLangWords() {
		return translationLangWords;
	}

	public void setTranslationLangWords(List<WordLexeme> translationLangWords) {
		this.translationLangWords = translationLangWords;
	}

	public String getTargetLangWordValue() {
		return targetLangWordValue;
	}

	public void setTargetLangWordValue(String targetLangWordValue) {
		this.targetLangWordValue = targetLangWordValue;
	}

	public List<WordDescript> getTargetLangWordCandidates() {
		return targetLangWordCandidates;
	}

	public void setTargetLangWordCandidates(List<WordDescript> targetLangWordCandidates) {
		this.targetLangWordCandidates = targetLangWordCandidates;
	}

	public List<WordLexeme> getTargetLangWords() {
		return targetLangWords;
	}

	public void setTargetLangWords(List<WordLexeme> targetLangWords) {
		this.targetLangWords = targetLangWords;
	}

	public boolean isInexactSynDefinitionMandatory() {
		return isInexactSynDefinitionMandatory;
	}

	public void setInexactSynDefinitionMandatory(boolean inexactSynDefinitionMandatory) {
		isInexactSynDefinitionMandatory = inexactSynDefinitionMandatory;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean complete) {
		isComplete = complete;
	}
}
