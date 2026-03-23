package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class CollocMemberId extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long collocLexemeId;

	private Long memberLexemeId;

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

}
