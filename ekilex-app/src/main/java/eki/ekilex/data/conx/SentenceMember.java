package eki.ekilex.data.conx;

import eki.common.data.AbstractDataObject;

public class SentenceMember extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long sentenceId;

	private Long constructMemberId;

	private String value;

	private Long memberSentenceId;

	private Long memberLexemeId;

	private Long memberFormId;

	private String posGroupCode;

	private String deprelCode;

	private String memberRole;

	private Integer memberOrder;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSentenceId() {
		return sentenceId;
	}

	public void setSentenceId(Long sentenceId) {
		this.sentenceId = sentenceId;
	}

	public Long getConstructMemberId() {
		return constructMemberId;
	}

	public void setConstructMemberId(Long constructMemberId) {
		this.constructMemberId = constructMemberId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getMemberSentenceId() {
		return memberSentenceId;
	}

	public void setMemberSentenceId(Long memberSentenceId) {
		this.memberSentenceId = memberSentenceId;
	}

	public Long getMemberLexemeId() {
		return memberLexemeId;
	}

	public void setMemberLexemeId(Long memberLexemeId) {
		this.memberLexemeId = memberLexemeId;
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

	public String getDeprelCode() {
		return deprelCode;
	}

	public void setDeprelCode(String deprelCode) {
		this.deprelCode = deprelCode;
	}

	public String getMemberRole() {
		return memberRole;
	}

	public void setMemberRole(String memberRole) {
		this.memberRole = memberRole;
	}

	public Integer getMemberOrder() {
		return memberOrder;
	}

	public void setMemberOrder(Integer memberOrder) {
		this.memberOrder = memberOrder;
	}

}
