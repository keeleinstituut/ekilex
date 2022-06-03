package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TypeWordRelMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private List<String> definitions;

	private List<String> usages;

	private List<String> lexRegisterCodes;

	private List<String> lexPosCodes;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public List<String> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
	}

	public List<String> getUsages() {
		return usages;
	}

	public void setUsages(List<String> usages) {
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
