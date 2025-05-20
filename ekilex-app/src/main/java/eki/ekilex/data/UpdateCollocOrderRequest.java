package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class UpdateCollocOrderRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long collocLexemeId;

	private Long memberLexemeId;

	private String direction;

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

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

}
