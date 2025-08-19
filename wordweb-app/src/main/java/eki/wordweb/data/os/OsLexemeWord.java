package eki.wordweb.data.os;

import java.util.List;

import eki.common.data.Classifier;
import eki.wordweb.service.util.OsLexemeClassifiers;

public class OsLexemeWord extends OsWord implements OsLexemeClassifiers {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private String valueStateCode;

	private Classifier valueState;

	private List<String> registerCodes;

	private List<Classifier> registers;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	@Override
	public String getValueStateCode() {
		return valueStateCode;
	}

	public void setValueStateCode(String valueStateCode) {
		this.valueStateCode = valueStateCode;
	}

	public Classifier getValueState() {
		return valueState;
	}

	@Override
	public void setValueState(Classifier valueState) {
		this.valueState = valueState;
	}

	@Override
	public List<String> getRegisterCodes() {
		return registerCodes;
	}

	public void setRegisterCodes(List<String> registerCodes) {
		this.registerCodes = registerCodes;
	}

	public List<Classifier> getRegisters() {
		return registers;
	}

	@Override
	public void setRegisters(List<Classifier> registers) {
		this.registers = registers;
	}

}
