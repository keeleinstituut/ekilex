package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class Guid extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String word;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

}
