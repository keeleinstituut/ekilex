package eki.ekilex.data;

import java.util.List;

import eki.common.constant.RelationStatus;
import eki.common.data.AbstractDataObject;

public class SynRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1896105442587879210L;

	private Long id;

	private Long wordId;

	private Long oppositeWordId;

	private String word;

	private List<String> definitions;

	private Long orderBy;

	private RelationStatus relationStatus;

	private RelationStatus oppositeRelationStatus;

	private List<RelationParam> relationParams;

	private Integer homonymNumber;

	private boolean otherHomonymsExist;

	private boolean isPrefixoid;

	private boolean isSuffixoid;

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

	public List<String> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
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

	public Integer getHomonymNumber() {
		return homonymNumber;
	}

	public void setHomonymNumber(Integer homonymNumber) {
		this.homonymNumber = homonymNumber;
	}

	public boolean isOtherHomonymsExist() {
		return otherHomonymsExist;
	}

	public void setOtherHomonymsExist(boolean otherHomonymsExist) {
		this.otherHomonymsExist = otherHomonymsExist;
	}

	public boolean isPrefixoid() {
		return isPrefixoid;
	}

	public void setPrefixoid(boolean prefixoid) {
		isPrefixoid = prefixoid;
	}

	public boolean isSuffixoid() {
		return isSuffixoid;
	}

	public void setSuffixoid(boolean suffixoid) {
		isSuffixoid = suffixoid;
	}
}
