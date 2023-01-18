package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class InexactSynMeaningRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long targetMeaningId;

	private Long inexactSynMeaningId;

	private String datasetCode;

	private List<String> targetMeaningWordValues;

	private List<Definition> targetMeaningDefinitions;

	private String targetLang;

	private String targetLangWordValue;

	private Long targetLangWordId;

	private Long wordRelationId;

	private String translationLang;

	private String translationLangWordValue;

	private Long translationLangWordId;

	private String inexactSynDef;

	private List<String> inexactSynMeaningWordValues;

	private List<Definition> inexactSynMeaningDefinitions;

	private String relationType;

	private boolean isSearchEnabled;

	private boolean revertToPreviousStep;

	public Long getTargetMeaningId() {
		return targetMeaningId;
	}

	public void setTargetMeaningId(Long targetMeaningId) {
		this.targetMeaningId = targetMeaningId;
	}

	public Long getInexactSynMeaningId() {
		return inexactSynMeaningId;
	}

	public void setInexactSynMeaningId(Long inexactSynMeaningId) {
		this.inexactSynMeaningId = inexactSynMeaningId;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public List<String> getTargetMeaningWordValues() {
		return targetMeaningWordValues;
	}

	public void setTargetMeaningWordValues(List<String> targetMeaningWordValues) {
		this.targetMeaningWordValues = targetMeaningWordValues;
	}

	public List<Definition> getTargetMeaningDefinitions() {
		return targetMeaningDefinitions;
	}

	public void setTargetMeaningDefinitions(List<Definition> targetMeaningDefinitions) {
		this.targetMeaningDefinitions = targetMeaningDefinitions;
	}

	public String getTargetLang() {
		return targetLang;
	}

	public void setTargetLang(String targetLang) {
		this.targetLang = targetLang;
	}

	public String getTargetLangWordValue() {
		return targetLangWordValue;
	}

	public void setTargetLangWordValue(String targetLangWordValue) {
		this.targetLangWordValue = targetLangWordValue;
	}

	public Long getTargetLangWordId() {
		return targetLangWordId;
	}

	public void setTargetLangWordId(Long targetLangWordId) {
		this.targetLangWordId = targetLangWordId;
	}

	public Long getWordRelationId() {
		return wordRelationId;
	}

	public void setWordRelationId(Long wordRelationId) {
		this.wordRelationId = wordRelationId;
	}

	public String getTranslationLang() {
		return translationLang;
	}

	public void setTranslationLang(String translationLang) {
		this.translationLang = translationLang;
	}

	public String getTranslationLangWordValue() {
		return translationLangWordValue;
	}

	public void setTranslationLangWordValue(String translationLangWordValue) {
		this.translationLangWordValue = translationLangWordValue;
	}

	public Long getTranslationLangWordId() {
		return translationLangWordId;
	}

	public void setTranslationLangWordId(Long translationLangWordId) {
		this.translationLangWordId = translationLangWordId;
	}

	public String getInexactSynDef() {
		return inexactSynDef;
	}

	public void setInexactSynDef(String inexactSynDef) {
		this.inexactSynDef = inexactSynDef;
	}

	public List<String> getInexactSynMeaningWordValues() {
		return inexactSynMeaningWordValues;
	}

	public void setInexactSynMeaningWordValues(List<String> inexactSynMeaningWordValues) {
		this.inexactSynMeaningWordValues = inexactSynMeaningWordValues;
	}

	public List<Definition> getInexactSynMeaningDefinitions() {
		return inexactSynMeaningDefinitions;
	}

	public void setInexactSynMeaningDefinitions(List<Definition> inexactSynMeaningDefinitions) {
		this.inexactSynMeaningDefinitions = inexactSynMeaningDefinitions;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public boolean isSearchEnabled() {
		return isSearchEnabled;
	}

	public void setSearchEnabled(boolean searchEnabled) {
		isSearchEnabled = searchEnabled;
	}

	public boolean isRevertToPreviousStep() {
		return revertToPreviousStep;
	}

	public void setRevertToPreviousStep(boolean revertToPreviousStep) {
		this.revertToPreviousStep = revertToPreviousStep;
	}
}
