package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class Word extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String lang;

	private String formsString;

	private String[] components;

	private String displayForm;

	private String vocalForm;

	private int homonymNr;

	private String morphCode;

	private String displayMorph;

	private String guid;

	public Word() {
	}

	public Word(String value, String lang, String formsString, String[] components, String displayForm, String vocalForm, int homonymNr, String morphCode, String guid) {
		this.value = value;
		this.lang = lang;
		this.formsString = formsString;
		this.components = components;
		this.displayForm = displayForm;
		this.vocalForm = vocalForm;
		this.homonymNr = homonymNr;
		this.morphCode = morphCode;
		this.guid = guid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
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

	public int getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(int homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

	public String getDisplayMorph() {
		return displayMorph;
	}

	public void setDisplayMorph(String displayMorph) {
		this.displayMorph = displayMorph;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

}
