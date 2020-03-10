package eki.wordweb.data;

import eki.common.constant.Complexity;
import eki.common.data.Classifier;

public class TypeWordRelation extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private Complexity[] lexComplexities;

	private String wordRelTypeCode;

	private Classifier wordRelType;

	public Complexity[] getLexComplexities() {
		return lexComplexities;
	}

	public void setLexComplexities(Complexity[] lexComplexities) {
		this.lexComplexities = lexComplexities;
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

}
