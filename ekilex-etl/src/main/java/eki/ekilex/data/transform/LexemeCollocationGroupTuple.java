package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class LexemeCollocationGroupTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long posGroupId;

	private String posGroupCode;

	private Long posGroupOrderBy;

	private Long relGroupId;

	private String relGroupName;

	private Float relGroupFrequency;

	private Float relGroupScore;

	private Long relGroupOrderBy;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
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
