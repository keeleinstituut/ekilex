package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class WordForm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String wordValue;

	private String formValue;

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public String getFormValue() {
		return formValue;
	}

	public void setFormValue(String formValue) {
		this.formValue = formValue;
	}

}
