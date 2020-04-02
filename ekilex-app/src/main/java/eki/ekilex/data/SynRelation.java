package eki.ekilex.data;

import java.util.List;

import eki.common.constant.RelationStatus;
import eki.common.data.AbstractDataObject;

public class SynRelation extends AbstractDataObject implements DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long id;

	private RelationStatus relationStatus;

	private RelationStatus oppositeRelationStatus;

	private List<TypeWordRelParam> relationParams;

	private Long orderBy;

	private Long wordId;

	private String wordValue;

	private String wordValuePrese;

	private Integer wordHomonymNr;

	private boolean homonymsExist;

	private String wordLang;

	private String[] wordTypeCodes;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean foreign;

	private List<String> wordDefinitions;

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

	public RelationStatus getOppositeRelationStatus() {
		return oppositeRelationStatus;
	}

	public void setOppositeRelationStatus(RelationStatus oppositeRelationStatus) {
		this.oppositeRelationStatus = oppositeRelationStatus;
	}

	public List<TypeWordRelParam> getRelationParams() {
		return relationParams;
	}

	public void setRelationParams(List<TypeWordRelParam> relationParams) {
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

	@Override
	public String getWordValue() {
		return wordValue;
	}

	@Override
	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	@Override
	public String getWordValuePrese() {
		return wordValuePrese;
	}

	@Override
	public void setWordValuePrese(String wordValuePrese) {
		this.wordValuePrese = wordValuePrese;
	}

	public Integer getWordHomonymNr() {
		return wordHomonymNr;
	}

	public void setWordHomonymNr(Integer wordHomonymNr) {
		this.wordHomonymNr = wordHomonymNr;
	}

	public boolean isHomonymsExist() {
		return homonymsExist;
	}

	public void setHomonymsExist(boolean homonymsExist) {
		this.homonymsExist = homonymsExist;
	}

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

	@Override
	public String[] getWordTypeCodes() {
		return wordTypeCodes;
	}

	@Override
	public void setWordTypeCodes(String[] wordTypeCodes) {
		this.wordTypeCodes = wordTypeCodes;
	}

	@Override
	public boolean isPrefixoid() {
		return prefixoid;
	}

	@Override
	public void setPrefixoid(boolean prefixoid) {
		this.prefixoid = prefixoid;
	}

	@Override
	public boolean isSuffixoid() {
		return suffixoid;
	}

	@Override
	public void setSuffixoid(boolean suffixoid) {
		this.suffixoid = suffixoid;
	}

	@Override
	public boolean isForeign() {
		return foreign;
	}

	@Override
	public void setForeign(boolean foreign) {
		this.foreign = foreign;
	}

	public List<String> getWordDefinitions() {
		return wordDefinitions;
	}

	public void setWordDefinitions(List<String> wordDefinitions) {
		this.wordDefinitions = wordDefinitions;
	}

}
