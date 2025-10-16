package eki.ekilex.data.migra;

import java.math.BigDecimal;

import eki.common.data.AbstractDataObject;

public class CollocMember extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long collocLexemeId;

	private Long memberLexemeId;

	private Long conjunctLexemeId;

	private Long memberFormId;

	private String posGroupCode;

	private String relGroupCode;

	private BigDecimal weight;

	private Integer memberOrder;

	private Integer groupOrder;

	private String hash;

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

	public Long getConjunctLexemeId() {
		return conjunctLexemeId;
	}

	public void setConjunctLexemeId(Long conjunctLexemeId) {
		this.conjunctLexemeId = conjunctLexemeId;
	}

	public Long getMemberFormId() {
		return memberFormId;
	}

	public void setMemberFormId(Long memberFormId) {
		this.memberFormId = memberFormId;
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

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
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

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
