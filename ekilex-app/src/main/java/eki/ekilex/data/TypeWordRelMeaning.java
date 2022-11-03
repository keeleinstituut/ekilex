package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TypeWordRelMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private Long lexemeId;

	private List<String> definitionValues;

	private List<Definition> definitions;

	private List<String> usageValues;

	private List<Usage> usages;

	private List<String> lexRegisterCodes;

	private List<String> lexPosCodes;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public List<String> getDefinitionValues() {
		return definitionValues;
	}

	public void setDefinitionValues(List<String> definitionValues) {
		this.definitionValues = definitionValues;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	public List<String> getUsageValues() {
		return usageValues;
	}

	public void setUsageValues(List<String> usageValues) {
		this.usageValues = usageValues;
	}

	public List<Usage> getUsages() {
		return usages;
	}

	public void setUsages(List<Usage> usages) {
		this.usages = usages;
	}

	public List<String> getLexRegisterCodes() {
		return lexRegisterCodes;
	}

	public void setLexRegisterCodes(List<String> lexRegisterCodes) {
		this.lexRegisterCodes = lexRegisterCodes;
	}

	public List<String> getLexPosCodes() {
		return lexPosCodes;
	}

	public void setLexPosCodes(List<String> lexPosCodes) {
		this.lexPosCodes = lexPosCodes;
	}
}
