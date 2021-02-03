package eki.wordweb.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.constant.SynonymType;
import eki.common.data.Classifier;

public class Synonym extends WordTypeData implements ComplexityType {

	private static final long serialVersionUID = 1L;

	private SynonymType type;

	private Long meaningId;

	private Long relationId;

	private Long lexemeId;

	private List<String> lexRegisterCodes;

	private List<Classifier> lexRegisters;

	private List<String> lexGovernmentValues;

	private List<TypeFreeform> lexGovernments;

	private String lexValueStateCode;

	private Classifier lexValueState;

	private Complexity complexity;

	private Float weight;

	private boolean additionalDataExists;

	public SynonymType getType() {
		return type;
	}

	public void setType(SynonymType type) {
		this.type = type;
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

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public List<String> getLexRegisterCodes() {
		return lexRegisterCodes;
	}

	public void setLexRegisterCodes(List<String> lexRegisterCodes) {
		this.lexRegisterCodes = lexRegisterCodes;
	}

	public List<Classifier> getLexRegisters() {
		return lexRegisters;
	}

	public void setLexRegisters(List<Classifier> lexRegisters) {
		this.lexRegisters = lexRegisters;
	}

	public List<String> getLexGovernmentValues() {
		return lexGovernmentValues;
	}

	public void setLexGovernmentValues(List<String> lexGovernmentValues) {
		this.lexGovernmentValues = lexGovernmentValues;
	}

	public List<TypeFreeform> getLexGovernments() {
		return lexGovernments;
	}

	public void setLexGovernments(List<TypeFreeform> lexGovernments) {
		this.lexGovernments = lexGovernments;
	}

	public String getLexValueStateCode() {
		return lexValueStateCode;
	}

	public void setLexValueStateCode(String lexValueStateCode) {
		this.lexValueStateCode = lexValueStateCode;
	}

	public Classifier getLexValueState() {
		return lexValueState;
	}

	public void setLexValueState(Classifier lexValueState) {
		this.lexValueState = lexValueState;
	}

	@Override
	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public boolean isAdditionalDataExists() {
		return additionalDataExists;
	}

	public void setAdditionalDataExists(boolean additionalDataExists) {
		this.additionalDataExists = additionalDataExists;
	}
}
