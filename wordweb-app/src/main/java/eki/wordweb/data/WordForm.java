package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class WordForm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String word;

	private String form;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

}
