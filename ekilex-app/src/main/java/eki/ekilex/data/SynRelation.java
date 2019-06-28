package eki.ekilex.data;

import java.util.List;

import eki.common.constant.RelationStatus;
import eki.common.data.AbstractDataObject;

public class SynRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1896105442587879210L;

	private Long id;

	private String word;

	private Long orderBy;

	private RelationStatus relationStatus;

	private RelationStatus oppositeRelationStatus;

	private List<RelationParam> relationParams;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<RelationParam> getRelationParams() {
		return relationParams;
	}

	public void setRelationParams(List<RelationParam> relationParams) {
		this.relationParams = relationParams;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}
}
