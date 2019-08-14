package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Word extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String guid;

	private String lang;

	private String value;

	private int homonymNr;

	private String wordClass;

	private String displayMorph;

	private String genderCode;

	private List<String> wordTypeCodes;

	//form properties

	private String morphCode;

	private String formsString;

	private String[] components;

	private String displayForm;

	private String vocalForm;

	private String aspectCode;

	public Word() {
	}

	public Word(String value, String lang, int homonymNr, String guid, String morphCode) {
		this.value = value;
		this.lang = lang;
		this.homonymNr = homonymNr;
		this.morphCode = morphCode;
		this.guid = guid;
	}

	public Word(String value, String lang, int homonymNr, String guid, String morphCode, String formsString, String[] components, String displayForm, String vocalForm, List<String> wordTypeCodes) {
		this.value = value;
		this.lang = lang;
		this.formsString = formsString;
		this.components = components;
		this.displayForm = displayForm;
		this.vocalForm = vocalForm;
		this.homonymNr = homonymNr;
		this.morphCode = morphCode;
		this.guid = guid;
		this.wordTypeCodes = wordTypeCodes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(int homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getWordClass() {
		return wordClass;
	}

	public void setWordClass(String wordClass) {
		this.wordClass = wordClass;
	}

	public String getDisplayMorph() {
		return displayMorph;
	}

	public void setDisplayMorph(String displayMorph) {
		this.displayMorph = displayMorph;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	public List<String> getWordTypeCodes() {
		return wordTypeCodes;
	}

	public void setWordTypeCodes(List<String> wordTypeCodes) {
		this.wordTypeCodes = wordTypeCodes;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

	public String getFormsString() {
		return formsString;
	}

	public void setFormsString(String formsString) {
		this.formsString = formsString;
	}

	public String[] getComponents() {
		return components;
	}

	public void setComponents(String[] components) {
		this.components = components;
	}

	public String getDisplayForm() {
		return displayForm;
	}

	public void setDisplayForm(String displayForm) {
		this.displayForm = displayForm;
	}

	public String getVocalForm() {
		return vocalForm;
	}

	public void setVocalForm(String vocalForm) {
		this.vocalForm = vocalForm;
	}

	public String getAspectCode() {
		return aspectCode;
	}

	public void setAspectCode(String aspectCode) {
		this.aspectCode = aspectCode;
	}

}
