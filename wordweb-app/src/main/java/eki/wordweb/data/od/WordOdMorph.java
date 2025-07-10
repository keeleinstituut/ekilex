package eki.wordweb.data.od;

import eki.common.data.AbstractDataObject;

public class WordOdMorph extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long wordOdMorphId;

	private String value;

	private String valuePrese;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getWordOdMorphId() {
		return wordOdMorphId;
	}

	public void setWordOdMorphId(Long wordOdMorphId) {
		this.wordOdMorphId = wordOdMorphId;
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
