package eki.ekilex.data;

import java.util.List;

public class MeaningRelation extends AbstractRelation {

	private static final long serialVersionUID = 1L;

	private List<String> lexemeValueStateCodes;

	private List<String> lexemeRegisterCodes;

	private List<String> lexemeGovernmentValues;

	private String lexemeLevels;

	private List<String> datasetCodes;

	private Float weight;

	public List<String> getLexemeValueStateCodes() {
		return lexemeValueStateCodes;
	}

	public void setLexemeValueStateCodes(List<String> lexemeValueStateCodes) {
		this.lexemeValueStateCodes = lexemeValueStateCodes;
	}

	public List<String> getLexemeRegisterCodes() {
		return lexemeRegisterCodes;
	}

	public void setLexemeRegisterCodes(List<String> lexemeRegisterCodes) {
		this.lexemeRegisterCodes = lexemeRegisterCodes;
	}

	public List<String> getLexemeGovernmentValues() {
		return lexemeGovernmentValues;
	}

	public void setLexemeGovernmentValues(List<String> lexemeGovernmentValues) {
		this.lexemeGovernmentValues = lexemeGovernmentValues;
	}

	public String getLexemeLevels() {
		return lexemeLevels;
	}

	public void setLexemeLevels(String lexemeLevels) {
		this.lexemeLevels = lexemeLevels;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}
}
