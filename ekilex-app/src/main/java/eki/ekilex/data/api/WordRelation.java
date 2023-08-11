package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class WordRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long wordId;

	private Long targetWordId;

	private String relationTypeCode;

	private String oppositeRelationTypeCode;

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

	public Long getTargetWordId() {
		return targetWordId;
	}

	public void setTargetWordId(Long targetWordId) {
		this.targetWordId = targetWordId;
	}

	public String getRelationTypeCode() {
		return relationTypeCode;
	}

	public void setRelationTypeCode(String relationTypeCode) {
		this.relationTypeCode = relationTypeCode;
	}

	public String getOppositeRelationTypeCode() {
		return oppositeRelationTypeCode;
	}

	public void setOppositeRelationTypeCode(String oppositeRelationTypeCode) {
		this.oppositeRelationTypeCode = oppositeRelationTypeCode;
	}
}
