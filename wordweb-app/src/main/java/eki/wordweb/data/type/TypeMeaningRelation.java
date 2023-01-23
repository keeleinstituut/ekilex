package eki.wordweb.data.type;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.Classifier;
import eki.wordweb.data.ComplexityType;
import eki.wordweb.data.WordTypeData;

public class TypeMeaningRelation extends WordTypeData implements ComplexityType {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private Complexity complexity;

	private Float weight;

	private String inexactSynDef;

	private List<String> lexValueStateCodes;

	private List<Classifier> lexValueStates;

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

	public String getInexactSynDef() {
		return inexactSynDef;
	}

	public void setInexactSynDef(String inexactSynDef) {
		this.inexactSynDef = inexactSynDef;
	}

	public List<String> getLexValueStateCodes() {
		return lexValueStateCodes;
	}

	public void setLexValueStateCodes(List<String> lexValueStateCodes) {
		this.lexValueStateCodes = lexValueStateCodes;
	}

	public List<Classifier> getLexValueStates() {
		return lexValueStates;
	}

	public void setLexValueStates(List<Classifier> lexValueStates) {
		this.lexValueStates = lexValueStates;
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
