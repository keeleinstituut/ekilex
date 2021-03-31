package eki.wordweb.data.type;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.constant.RelationStatus;
import eki.common.data.Classifier;
import eki.wordweb.data.WordTypeData;

public class TypeWordRelation extends WordTypeData {

	private static final long serialVersionUID = 1L;

	private Long wordGroupId;

	private String wordRelTypeCode;

	private Classifier wordRelType;

	private RelationStatus relationStatus;

	private Long orderBy;

	private List<Complexity> lexComplexities;

	private boolean homonymsExist;

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

	public RelationStatus getRelationStatus() {
		return relationStatus;
	}

	public void setRelationStatus(RelationStatus relationStatus) {
		this.relationStatus = relationStatus;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public List<Complexity> getLexComplexities() {
		return lexComplexities;
	}

	public void setLexComplexities(List<Complexity> lexComplexities) {
		this.lexComplexities = lexComplexities;
	}

	public boolean isHomonymsExist() {
		return homonymsExist;
	}

	public void setHomonymsExist(boolean homonymsExist) {
		this.homonymsExist = homonymsExist;
	}

}
