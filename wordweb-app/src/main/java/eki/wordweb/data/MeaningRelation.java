package eki.wordweb.data;

import java.util.List;

import eki.common.data.Classifier;

public class MeaningRelation extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private Float weight;

	private String nearSynDefinitionValue;

	private String lexValueStateCode;

	private Classifier lexValueState;

	private List<String> lexRegisterCodes;

	private List<Classifier> lexRegisters;

	private List<String> lexGovernmentValues;

	private String meaningRelTypeCode;

	private Classifier meaningRelType;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public String getNearSynDefinitionValue() {
		return nearSynDefinitionValue;
	}

	public void setNearSynDefinitionValue(String nearSynDefinitionValue) {
		this.nearSynDefinitionValue = nearSynDefinitionValue;
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

	public String getMeaningRelTypeCode() {
		return meaningRelTypeCode;
	}

	public void setMeaningRelTypeCode(String meaningRelTypeCode) {
		this.meaningRelTypeCode = meaningRelTypeCode;
	}

	public Classifier getMeaningRelType() {
		return meaningRelType;
	}

	public void setMeaningRelType(Classifier meaningRelType) {
		this.meaningRelType = meaningRelType;
	}

}
