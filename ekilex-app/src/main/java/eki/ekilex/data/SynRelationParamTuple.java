package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.constant.RelationStatus;
import eki.common.data.AbstractDataObject;

public class SynRelationParamTuple extends AbstractDataObject {

	private static final long serialVersionUID = 4400465324244993091L;

	@Column(name= "relation_id")
	private Long relationId;

	@Column(name= "word")
	private String word;

	@Column(name= "relation_status")
	private RelationStatus relationStatus;

	@Column(name= "opposite_relation_status")
	private RelationStatus oppositeRelationStatus;

	@Column(name= "param_name")
	private String paramName;

	@Column(name= "param_value")
	private String paramValue;

	public Long getRelationId() {
		return relationId;
	}

	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public RelationStatus getRelationStatus() {
		return relationStatus;
	}

	public void setRelationStatus(RelationStatus relationStatus) {
		this.relationStatus = relationStatus;
	}

	public RelationStatus getOppositeRelationStatus() {
		return oppositeRelationStatus;
	}

	public void setOppositeRelationStatus(RelationStatus oppositeRelationStatus) {
		this.oppositeRelationStatus = oppositeRelationStatus;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
}
