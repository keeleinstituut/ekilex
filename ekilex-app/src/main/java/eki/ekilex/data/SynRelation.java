package eki.ekilex.data;

import java.util.List;

import eki.common.constant.RelationStatus;
import eki.common.data.AbstractDataObject;

public class SynRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1896105442587879210L;

	private Long id;

	private RelationStatus relationStatus;

	private Long orderBy;

	private RelationStatus oppositeRelationStatus;

	private Long relatedWordId;

	private String relatedWord;

	private Integer relatedWordHomonymNr;

	private String relatedWordLang;

	private List<RelationParam> relationParams;

	private List<String> relatedWordDefinitions;

	private boolean relatedWordIsPrefixoid;

	private boolean relatedWordIsSuffixoid;

	private boolean relatedWordHomonymsExist;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public RelationStatus getOppositeRelationStatus() {
		return oppositeRelationStatus;
	}

	public void setOppositeRelationStatus(RelationStatus oppositeRelationStatus) {
		this.oppositeRelationStatus = oppositeRelationStatus;
	}

	public Long getRelatedWordId() {
		return relatedWordId;
	}

	public void setRelatedWordId(Long relatedWordId) {
		this.relatedWordId = relatedWordId;
	}

	public String getRelatedWord() {
		return relatedWord;
	}

	public void setRelatedWord(String relatedWord) {
		this.relatedWord = relatedWord;
	}

	public Integer getRelatedWordHomonymNr() {
		return relatedWordHomonymNr;
	}

	public void setRelatedWordHomonymNr(Integer relatedWordHomonymNr) {
		this.relatedWordHomonymNr = relatedWordHomonymNr;
	}

	public String getRelatedWordLang() {
		return relatedWordLang;
	}

	public void setRelatedWordLang(String relatedWordLang) {
		this.relatedWordLang = relatedWordLang;
	}

	public List<RelationParam> getRelationParams() {
		return relationParams;
	}

	public void setRelationParams(List<RelationParam> relationParams) {
		this.relationParams = relationParams;
	}

	public List<String> getRelatedWordDefinitions() {
		return relatedWordDefinitions;
	}

	public void setRelatedWordDefinitions(List<String> relatedWordDefinitions) {
		this.relatedWordDefinitions = relatedWordDefinitions;
	}

	public boolean isRelatedWordIsPrefixoid() {
		return relatedWordIsPrefixoid;
	}

	public void setRelatedWordIsPrefixoid(boolean relatedWordIsPrefixoid) {
		this.relatedWordIsPrefixoid = relatedWordIsPrefixoid;
	}

	public boolean isRelatedWordIsSuffixoid() {
		return relatedWordIsSuffixoid;
	}

	public void setRelatedWordIsSuffixoid(boolean relatedWordIsSuffixoid) {
		this.relatedWordIsSuffixoid = relatedWordIsSuffixoid;
	}

	public boolean isRelatedWordHomonymsExist() {
		return relatedWordHomonymsExist;
	}

	public void setRelatedWordHomonymsExist(boolean relatedWordHomonymsExist) {
		this.relatedWordHomonymsExist = relatedWordHomonymsExist;
	}

}
