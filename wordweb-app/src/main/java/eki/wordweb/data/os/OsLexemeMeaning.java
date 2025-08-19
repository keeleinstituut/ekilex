package eki.wordweb.data.os;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;
import eki.wordweb.service.util.OsLexemeClassifiers;

public class OsLexemeMeaning extends AbstractDataObject implements OsLexemeClassifiers {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long lexemeId;

	private Long meaningId;

	private String valueStateCode;

	private Classifier valueState;

	private List<String> registerCodes;

	private List<Classifier> registers;

	private OsMeaning meaning;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
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

	public OsMeaning getMeaning() {
		return meaning;
	}

	public void setMeaning(OsMeaning meaning) {
		this.meaning = meaning;
	}

}
