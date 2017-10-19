package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class Word extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String lang;

	private String[] components;

	private String displayForm;

	private String vocalForm;

	private int homonymNr;

	private String morphCode;

	public Word(String value, String lang, String[] components, String displayForm, String vocalForm, int homonymNr, String morphCode) {
		super();
		this.value = value;
		this.lang = lang;
		this.components = components;
		this.displayForm = displayForm;
		this.vocalForm = vocalForm;
		this.homonymNr = homonymNr;
		this.morphCode = morphCode;
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

	public String getLang() {
		return lang;
	}

	public String[] getComponents() {
		return components;
	}

	public String getDisplayForm() {
		return displayForm;
	}

	public String getVocalForm() {
		return vocalForm;
	}

	public int getHomonymNr() {
		return homonymNr;
	}

	public String getMorphCode() {
		return morphCode;
	}

}
