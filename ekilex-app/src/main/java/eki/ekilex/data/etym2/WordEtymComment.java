package eki.ekilex.data.etym2;

import eki.common.data.AbstractDataObject;

public class WordEtymComment extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long wordEtymId;

	private String value;

	private String valuePrese;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWordEtymId() {
		return wordEtymId;
	}

	public void setWordEtymId(Long wordEtymId) {
		this.wordEtymId = wordEtymId;
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
