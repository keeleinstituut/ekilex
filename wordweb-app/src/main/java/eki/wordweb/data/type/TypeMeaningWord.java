package eki.wordweb.data.type;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import eki.common.constant.Complexity;
import eki.common.constant.SynonymType;
import eki.common.data.Classifier;
import eki.wordweb.data.ComplexityType;
import eki.wordweb.data.WordTypeData;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TypeMeaningWord extends WordTypeData implements ComplexityType {

	private static final long serialVersionUID = 1L;

	private SynonymType type;

	private Long lexemeId;

	private Long meaningId;

	private Long relationId;

	private Long mwLexemeId;

	private Complexity mwLexComplexity;

	private Float mwLexWeight;

	private List<String> mwLexGovernmentValues;

	private List<TypeValueEntity> mwLexGovernments;

	private List<String> mwLexRegisterCodes;

	private List<Classifier> mwLexRegisters;

	private String mwLexValueStateCode;

	private Classifier mwLexValueState;

	private String inexactSynMeaningDefinition;

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

	@Override
	public Complexity getComplexity() {
		return mwLexComplexity;
	}

	public Complexity getMwLexComplexity() {
		return mwLexComplexity;
	}

	public void setMwLexComplexity(Complexity mwLexComplexity) {
		this.mwLexComplexity = mwLexComplexity;
	}

	public Float getMwLexWeight() {
		return mwLexWeight;
	}

	public void setMwLexWeight(Float mwLexWeight) {
		this.mwLexWeight = mwLexWeight;
	}

	public List<String> getMwLexGovernmentValues() {
		return mwLexGovernmentValues;
	}

	public void setMwLexGovernmentValues(List<String> mwLexGovernmentValues) {
		this.mwLexGovernmentValues = mwLexGovernmentValues;
	}

	public List<TypeValueEntity> getMwLexGovernments() {
		return mwLexGovernments;
	}

	public void setMwLexGovernments(List<TypeValueEntity> mwLexGovernments) {
		this.mwLexGovernments = mwLexGovernments;
	}

	public List<String> getMwLexRegisterCodes() {
		return mwLexRegisterCodes;
	}

	public void setMwLexRegisterCodes(List<String> mwLexRegisterCodes) {
		this.mwLexRegisterCodes = mwLexRegisterCodes;
	}

	public List<Classifier> getMwLexRegisters() {
		return mwLexRegisters;
	}

	public void setMwLexRegisters(List<Classifier> mwLexRegisters) {
		this.mwLexRegisters = mwLexRegisters;
	}

	public String getMwLexValueStateCode() {
		return mwLexValueStateCode;
	}

	public void setMwLexValueStateCode(String mwLexValueStateCode) {
		this.mwLexValueStateCode = mwLexValueStateCode;
	}

	public Classifier getMwLexValueState() {
		return mwLexValueState;
	}

	public void setMwLexValueState(Classifier mwLexValueState) {
		this.mwLexValueState = mwLexValueState;
	}

	public String getInexactSynMeaningDefinition() {
		return inexactSynMeaningDefinition;
	}

	public void setInexactSynMeaningDefinition(String inexactSynMeaningDefinition) {
		this.inexactSynMeaningDefinition = inexactSynMeaningDefinition;
	}

	public boolean isAdditionalDataExists() {
		return additionalDataExists;
	}

	public void setAdditionalDataExists(boolean additionalDataExists) {
		this.additionalDataExists = additionalDataExists;
	}

}
