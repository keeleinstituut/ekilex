package eki.ekilex.data.etym2;

import eki.common.data.AbstractDataObject;

public class WordEtym extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long wordId;

	private String etymologyTypeCode;

	private String etymologyYear;

	private boolean questionable;

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

	public String getEtymologyTypeCode() {
		return etymologyTypeCode;
	}

	public void setEtymologyTypeCode(String etymologyTypeCode) {
		this.etymologyTypeCode = etymologyTypeCode;
	}

	public String getEtymologyYear() {
		return etymologyYear;
	}

	public void setEtymologyYear(String etymologyYear) {
		this.etymologyYear = etymologyYear;
	}

	public boolean isQuestionable() {
		return questionable;
	}

	public void setQuestionable(boolean questionable) {
		this.questionable = questionable;
	}

}
