package eki.wwexam.data.os;

import eki.common.data.AbstractDataObject;

public class WordOsMorph extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long wordOsMorphId;

	private String value;

	private String valuePrese;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getWordOsMorphId() {
		return wordOsMorphId;
	}

	public void setWordOsMorphId(Long wordOsMorphId) {
		this.wordOsMorphId = wordOsMorphId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

}
