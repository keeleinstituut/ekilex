package eki.wordweb.data;

import java.util.List;

import eki.common.constant.SynonymType;
import eki.common.data.Classifier;

public class MeaningWord extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private SynonymType type;

	private Long lexemeId;

	private Long meaningId;

	private Long relationId;

	private Long mwLexemeId;

	private Float mwLexemeWeight;

	private List<String> mwLexemeGovernmentValues;

	private List<Government> mwLexemeGovernments;

	private List<String> mwLexemeRegisterCodes;

	private List<Classifier> mwLexemeRegisters;

	private String mwLexemeValueStateCode;

	private Classifier mwLexemeValueState;

	private String nearSynDefinitionValue;

	private boolean additionalDataExists;

	public SynonymType getType() {
		return type;
	}

	public void setType(SynonymType type) {
		this.type = type;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public Long getRelationId() {
		return relationId;
	}

	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}

	public Long getMwLexemeId() {
		return mwLexemeId;
	}

	public void setMwLexemeId(Long mwLexemeId) {
		this.mwLexemeId = mwLexemeId;
	}

	public Float getMwLexemeWeight() {
		return mwLexemeWeight;
	}

	public void setMwLexemeWeight(Float mwLexemeWeight) {
		this.mwLexemeWeight = mwLexemeWeight;
	}

	public List<String> getMwLexemeGovernmentValues() {
		return mwLexemeGovernmentValues;
	}

	public void setMwLexemeGovernmentValues(List<String> mwLexemeGovernmentValues) {
		this.mwLexemeGovernmentValues = mwLexemeGovernmentValues;
	}

	public List<Government> getMwLexemeGovernments() {
		return mwLexemeGovernments;
	}

	public void setMwLexemeGovernments(List<Government> mwLexemeGovernments) {
		this.mwLexemeGovernments = mwLexemeGovernments;
	}

	public List<String> getMwLexemeRegisterCodes() {
		return mwLexemeRegisterCodes;
	}

	public void setMwLexemeRegisterCodes(List<String> mwLexemeRegisterCodes) {
		this.mwLexemeRegisterCodes = mwLexemeRegisterCodes;
	}

	public List<Classifier> getMwLexemeRegisters() {
		return mwLexemeRegisters;
	}

	public void setMwLexemeRegisters(List<Classifier> mwLexemeRegisters) {
		this.mwLexemeRegisters = mwLexemeRegisters;
	}

	public String getMwLexemeValueStateCode() {
		return mwLexemeValueStateCode;
	}

	public void setMwLexemeValueStateCode(String mwLexemeValueStateCode) {
		this.mwLexemeValueStateCode = mwLexemeValueStateCode;
	}

	public Classifier getMwLexemeValueState() {
		return mwLexemeValueState;
	}

	public void setMwLexemeValueState(Classifier mwLexemeValueState) {
		this.mwLexemeValueState = mwLexemeValueState;
	}

	public String getNearSynDefinitionValue() {
		return nearSynDefinitionValue;
	}

	public void setNearSynDefinitionValue(String nearSynDefinitionValue) {
		this.nearSynDefinitionValue = nearSynDefinitionValue;
	}

	public boolean isAdditionalDataExists() {
		return additionalDataExists;
	}

	public void setAdditionalDataExists(boolean additionalDataExists) {
		this.additionalDataExists = additionalDataExists;
	}

}
