package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class MeaningRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long meaningId;

	private Long targetMeaningId;

	private String relationTypeCode;

	private String oppositeRelationTypeCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long wordId) {
		this.meaningId = wordId;
	}

	public Long getTargetMeaningId() {
		return targetMeaningId;
	}

	public void setTargetMeaningId(Long targetWordId) {
		this.targetMeaningId = targetWordId;
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
