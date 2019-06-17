package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class LexemeCollocationTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexCollocId;

	private Long lexemeId;

	private Long collocationId;

	private String memberForm;

	private String conjunct;

	private Float weight;

	private Integer memberOrder;

	private Integer groupOrder;

	private Long posGroupId;

	private String posGroupCode;

	private Long posGroupOrderBy;

	private Long relGroupId;

	private String relGroupName;

	private Float relGroupFrequency;

	private Float relGroupScore;

	private Long relGroupOrderBy;

	public Long getLexCollocId() {
		return lexCollocId;
	}

	public void setLexCollocId(Long lexCollocId) {
		this.lexCollocId = lexCollocId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getCollocationId() {
		return collocationId;
	}

	public void setCollocationId(Long collocationId) {
		this.collocationId = collocationId;
	}

	public String getMemberForm() {
		return memberForm;
	}

	public void setMemberForm(String memberForm) {
		this.memberForm = memberForm;
	}

	public String getConjunct() {
		return conjunct;
	}

	public void setConjunct(String conjunct) {
		this.conjunct = conjunct;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Integer getMemberOrder() {
		return memberOrder;
	}

	public void setMemberOrder(Integer memberOrder) {
		this.memberOrder = memberOrder;
	}

	public Integer getGroupOrder() {
		return groupOrder;
	}

	public void setGroupOrder(Integer groupOrder) {
		this.groupOrder = groupOrder;
	}

	public Long getPosGroupId() {
		return posGroupId;
	}

	public void setPosGroupId(Long posGroupId) {
		this.posGroupId = posGroupId;
	}

	public String getPosGroupCode() {
		return posGroupCode;
	}

	public void setPosGroupCode(String posGroupCode) {
		this.posGroupCode = posGroupCode;
	}

	public Long getPosGroupOrderBy() {
		return posGroupOrderBy;
	}

	public void setPosGroupOrderBy(Long posGroupOrderBy) {
		this.posGroupOrderBy = posGroupOrderBy;
	}

	public Long getRelGroupId() {
		return relGroupId;
	}

	public void setRelGroupId(Long relGroupId) {
		this.relGroupId = relGroupId;
	}

	public String getRelGroupName() {
		return relGroupName;
	}

	public void setRelGroupName(String relGroupName) {
		this.relGroupName = relGroupName;
	}

	public Float getRelGroupFrequency() {
		return relGroupFrequency;
	}

	public void setRelGroupFrequency(Float relGroupFrequency) {
		this.relGroupFrequency = relGroupFrequency;
	}

	public Float getRelGroupScore() {
		return relGroupScore;
	}

	public void setRelGroupScore(Float relGroupScore) {
		this.relGroupScore = relGroupScore;
	}

	public Long getRelGroupOrderBy() {
		return relGroupOrderBy;
	}

	public void setRelGroupOrderBy(Long relGroupOrderBy) {
		this.relGroupOrderBy = relGroupOrderBy;
	}

}
