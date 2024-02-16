package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WordEtymSourceLink extends AbstractDataObject {

	private static final long serialVersionUID = 8896040227578617598L;

	private Long id;

	private Long wordEtymId;

	private Long sourceId;

	private String value;

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

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
