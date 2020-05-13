package eki.wordweb.data;

import eki.common.constant.Complexity;
import eki.common.data.Classifier;

public class TypeWordRelation extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private Long wordGroupId;

	private String wordRelTypeCode;

	private Classifier wordRelType;

	private Complexity[] lexComplexities;

	public Long getWordGroupId() {
		return wordGroupId;
	}

	public void setWordGroupId(Long wordGroupId) {
		this.wordGroupId = wordGroupId;
	}

	public String getWordRelTypeCode() {
		return wordRelTypeCode;
	}

	public void setWordRelTypeCode(String wordRelTypeCode) {
		this.wordRelTypeCode = wordRelTypeCode;
	}

	public Classifier getWordRelType() {
		return wordRelType;
	}

	public void setWordRelType(Classifier wordRelType) {
		this.wordRelType = wordRelType;
	}

	public Complexity[] getLexComplexities() {
		return lexComplexities;
	}

	public void setLexComplexities(Complexity[] lexComplexities) {
		this.lexComplexities = lexComplexities;
	}

}
