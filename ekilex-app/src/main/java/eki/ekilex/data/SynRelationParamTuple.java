package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.constant.RelationStatus;
import eki.common.data.AbstractDataObject;

public class SynRelationParamTuple extends AbstractDataObject {

	private static final long serialVersionUID = 4400465324244993091L;

	@Column(name = "relation_id")
	private Long relationId;

	@Column(name = "word_id")
	private Long wordId;

	@Column(name = "opposite_word_id")
	private Long oppositeWordId;

	@Column(name = "word")
	private String word;

	@Column(name = "relation_status")
	private RelationStatus relationStatus;

	@Column(name = "opposite_relation_status")
	private RelationStatus oppositeRelationStatus;

	@Column(name = "order_by")
	private Long orderBy;

	@Column(name = "param_name")
	private String paramName;

	@Column(name = "param_value")
	private String paramValue;

	@Column(name = "word_homonym_number")
	private Integer homonymNumber;

	@Column(name = "definition_value")
	private String definitionValue;

	@Column(name = "other_homonym_number")
	private Integer otherHomonymNumber;

	public Long getRelationId() {
		return relationId;
	}

	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getOppositeWordId() {
		return oppositeWordId;
	}

	public void setOppositeWordId(Long oppositeWordId) {
		this.oppositeWordId = oppositeWordId;
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

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
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

	public Integer getHomonymNumber() {
		return homonymNumber;
	}

	public void setHomonymNumber(Integer homonymNumber) {
		this.homonymNumber = homonymNumber;
	}

	public String getDefinitionValue() {
		return definitionValue;
	}

	public void setDefinitionValue(String definitionValue) {
		this.definitionValue = definitionValue;
	}

	public Integer getOtherHomonymNumber() {
		return otherHomonymNumber;
	}

	public void setOtherHomonymNumber(Integer otherHomonymNumber) {
		this.otherHomonymNumber = otherHomonymNumber;
	}
}