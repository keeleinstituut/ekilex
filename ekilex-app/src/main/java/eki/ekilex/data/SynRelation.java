package eki.ekilex.data;

import java.util.List;

public class SynRelation {

	private static final long serialVersionUID = 1896105442587879210L;

	private Relation wordRelation;

	private Relation oppositeRelation;

	private List<RelationParam> relationParams;

	public List<RelationParam> getRelationParams() {
		return relationParams;
	}

	public void setRelationParams(List<RelationParam> relationParams) {
		this.relationParams = relationParams;
	}

	public Relation getWordRelation() {
		return wordRelation;
	}

	public void setWordRelation(Relation wordRelation) {
		this.wordRelation = wordRelation;
	}

	public Relation getOppositeRelation() {
		return oppositeRelation;
	}

	public void setOppositeRelation(Relation oppositeRelation) {
		this.oppositeRelation = oppositeRelation;
	}
}
