package eki.wordweb.data;

import java.util.List;

import eki.common.data.Classifier;

public class MeaningWord extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String word;

	private Integer homonymNr;

	private String lang;

	private Classifier aspect;

	private List<Classifier> registers;

	private List<String> governments;

	private boolean emphasiseMatch;

	private boolean additionalDataExists;

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

	public Classifier getAspect() {
		return aspect;
	}

	public void setAspect(Classifier aspect) {
		this.aspect = aspect;
	}

	public List<Classifier> getRegisters() {
		return registers;
	}

	public void setRegisters(List<Classifier> registers) {
		this.registers = registers;
	}

	public List<String> getGovernments() {
		return governments;
	}

	public void setGovernments(List<String> governments) {
		this.governments = governments;
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
