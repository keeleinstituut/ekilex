package eki.ekilex.data.etym2;

import eki.common.data.AbstractDataObject;

public class WordEtym extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long wordId;

	private String etymologyYear;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getEtymologyYear() {
		return etymologyYear;
	}

	public void setEtymologyYear(String etymologyYear) {
		this.etymologyYear = etymologyYear;
	}

}
