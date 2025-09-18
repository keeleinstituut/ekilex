package eki.wordweb.data.os;

import eki.common.data.AbstractDataObject;

public class WordEkiRecommendation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long wordEkiRecommendationId;

	private String value;

	private String valuePrese;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getWordEkiRecommendationId() {
		return wordEkiRecommendationId;
	}

	public void setWordEkiRecommendationId(Long wordEkiRecommendationId) {
		this.wordEkiRecommendationId = wordEkiRecommendationId;
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
