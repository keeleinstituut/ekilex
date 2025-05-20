package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class CollocMemberOrder extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long collocLexemeId;

	private Long memberLexemeId;

	private String posGroupCode;

	private String relGroupCode;

	private Integer memberOrder;

	private Integer groupOrder;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCollocLexemeId() {
		return collocLexemeId;
	}

	public void setCollocLexemeId(Long collocLexemeId) {
		this.collocLexemeId = collocLexemeId;
	}

	public Long getMemberLexemeId() {
		return memberLexemeId;
	}

	public void setMemberLexemeId(Long memberLexemeId) {
		this.memberLexemeId = memberLexemeId;
	}

	public String getPosGroupCode() {
		return posGroupCode;
	}

	public void setPosGroupCode(String posGroupCode) {
		this.posGroupCode = posGroupCode;
	}

	public String getRelGroupCode() {
		return relGroupCode;
	}

	public void setRelGroupCode(String relGroupCode) {
		this.relGroupCode = relGroupCode;
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
