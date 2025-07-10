package eki.wordweb.data.od;

import eki.common.data.Classifier;

public class OdWordRelation extends OdWord {

	private static final long serialVersionUID = 1L;

	private Long wordRelationId;

	private Long relatedWordId;

	private String wordRelTypeCode;

	private Classifier wordRelType;

	public Long getWordRelationId() {
		return wordRelationId;
	}

	public void setWordRelationId(Long wordRelationId) {
		this.wordRelationId = wordRelationId;
	}

	public Long getRelatedWordId() {
		return relatedWordId;
	}

	public void setRelatedWordId(Long relatedWordId) {
		this.relatedWordId = relatedWordId;
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
