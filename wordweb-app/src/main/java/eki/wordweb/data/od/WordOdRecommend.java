package eki.wordweb.data.od;

import eki.common.data.AbstractDataObject;

public class WordOdRecommend extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long wordOdRecommendId;

	private String value;

	private String valuePrese;

	private String optValue;

	private String optValuePrese;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getWordOdRecommendId() {
		return wordOdRecommendId;
	}

	public void setWordOdRecommendId(Long wordOdRecommendId) {
		this.wordOdRecommendId = wordOdRecommendId;
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

	public String getOptValue() {
		return optValue;
	}

	public void setOptValue(String optValue) {
		this.optValue = optValue;
	}

	public String getOptValuePrese() {
		return optValuePrese;
	}

	public void setOptValuePrese(String optValuePrese) {
		this.optValuePrese = optValuePrese;
	}

}
