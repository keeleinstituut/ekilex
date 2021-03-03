package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class WordFreeform extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long freeformId;

	private String valuePrese;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getFreeformId() {
		return freeformId;
	}

	public void setFreeformId(Long freeformId) {
		this.freeformId = freeformId;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}
}
