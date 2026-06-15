package eki.ekilex.data.conx;

import java.math.BigDecimal;

import eki.common.data.AbstractDataObject;

public class ConstructMemberStat extends AbstractDataObject {

	private static final long serialVersionUID = -5751541742753283201L;

	private Long id;

	private Long constructMemberId;

	private Long lexemeId;

	private Long formId;

	private Long frequency;

	private BigDecimal salience;

	private String proficiencyLevelCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getConstructMemberId() {
		return constructMemberId;
	}

	public void setConstructMemberId(Long constructMemberId) {
		this.constructMemberId = constructMemberId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public Long getFrequency() {
		return frequency;
	}

	public void setFrequency(Long frequency) {
		this.frequency = frequency;
	}

	public BigDecimal getSalience() {
		return salience;
	}

	public void setSalience(BigDecimal salience) {
		this.salience = salience;
	}

	public String getProficiencyLevelCode() {
		return proficiencyLevelCode;
	}

	public void setProficiencyLevelCode(String proficiencyLevelCode) {
		this.proficiencyLevelCode = proficiencyLevelCode;
	}

}
