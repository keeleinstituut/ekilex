package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class LexemeCollocationTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long lexCollocId;

	private Long relGroupId;

	private Long collocationId;

	private String memberForm;

	private String conjunct;

	private Float weight;

	private Integer memberOrder;

	private Integer groupOrder;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getLexCollocId() {
		return lexCollocId;
	}

	public void setLexCollocId(Long lexCollocId) {
		this.lexCollocId = lexCollocId;
	}

	public Long getRelGroupId() {
		return relGroupId;
	}

	public void setRelGroupId(Long relGroupId) {
		this.relGroupId = relGroupId;
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

}
