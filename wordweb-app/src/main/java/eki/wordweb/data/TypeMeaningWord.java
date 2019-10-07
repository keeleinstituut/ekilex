package eki.wordweb.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.Classifier;

public class TypeMeaningWord extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private Long mwLexemeId;

	private Complexity mwLexComplexity;

	private List<TypeGovernment> mwLexGovernments;

	private List<String> mwLexRegisterCodes;

	private List<Classifier> mwLexRegisters;

	private Long wordId;

	private String word;

	private Integer homonymNr;

	private String lang;

	private String aspectCode;

	private Classifier aspect;

	private boolean emphasiseMatch;

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

	public Complexity getMwLexComplexity() {
		return mwLexComplexity;
	}

	public void setMwLexComplexity(Complexity mwLexComplexity) {
		this.mwLexComplexity = mwLexComplexity;
	}

	public List<TypeGovernment> getMwLexGovernments() {
		return mwLexGovernments;
	}

	public void setMwLexGovernments(List<TypeGovernment> mwLexGovernments) {
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

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
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

	public boolean isEmphasiseMatch() {
		return emphasiseMatch;
	}

	public void setEmphasiseMatch(boolean emphasiseMatch) {
		this.emphasiseMatch = emphasiseMatch;
	}

	public boolean isAdditionalDataExists() {
		return additionalDataExists;
	}

	public void setAdditionalDataExists(boolean additionalDataExists) {
		this.additionalDataExists = additionalDataExists;
	}

}
