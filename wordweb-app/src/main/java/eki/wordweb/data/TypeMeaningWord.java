package eki.wordweb.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.Classifier;

public class TypeMeaningWord extends WordTypeData implements ComplexityType {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private Long mwLexemeId;

	private Complexity mwLexComplexity;

	private Float mwLexWeight;

	private List<TypeFreeform> mwLexGovernments;

	private List<String> mwLexRegisterCodes;

	private List<Classifier> mwLexRegisters;

	private String mwLexValueStateCode;

	private Classifier mwLexValueState;

	private String aspectCode;

	private Classifier aspect;

	private boolean additionalDataExists;

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

	public List<TypeFreeform> getMwLexGovernments() {
		return mwLexGovernments;
	}

	public void setMwLexGovernments(List<TypeFreeform> mwLexGovernments) {
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

	public boolean isAdditionalDataExists() {
		return additionalDataExists;
	}

	public void setAdditionalDataExists(boolean additionalDataExists) {
		this.additionalDataExists = additionalDataExists;
	}

}
