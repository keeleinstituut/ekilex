package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class Form extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String displayForm;

	private String morphCode;

	private boolean isWord;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplayForm() {
		return displayForm;
	}

	public void setDisplayForm(String displayForm) {
		this.displayForm = displayForm;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

	public boolean isWord() {
		return isWord;
	}

	public void setWord(boolean isWord) {
		this.isWord = isWord;
	}

}
