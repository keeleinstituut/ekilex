package eki.ekilex.data.migra;

import eki.common.data.AbstractDataObject;

public class WordRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long word1Id;

	private String word1Value;

	private Long word2Id;

	private String word2Value;

	private String wordRelTypeCode;

	private Long orderBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWord1Id() {
		return word1Id;
	}

	public void setWord1Id(Long word1Id) {
		this.word1Id = word1Id;
	}

	public String getWord1Value() {
		return word1Value;
	}

	public void setWord1Value(String word1Value) {
		this.word1Value = word1Value;
	}

	public Long getWord2Id() {
		return word2Id;
	}

	public void setWord2Id(Long word2Id) {
		this.word2Id = word2Id;
	}

	public String getWord2Value() {
		return word2Value;
	}

	public void setWord2Value(String word2Value) {
		this.word2Value = word2Value;
	}

	public String getWordRelTypeCode() {
		return wordRelTypeCode;
	}

	public void setWordRelTypeCode(String wordRelTypeCode) {
		this.wordRelTypeCode = wordRelTypeCode;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

}
