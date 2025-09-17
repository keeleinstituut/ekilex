package eki.ekilex.data;

import java.math.BigDecimal;

import eki.common.data.AbstractDataObject;

public class CollocMember extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String conjunct;

	private Long lexemeId;

	private Long wordId;

	private String wordValue;

	private Integer homonymNr;

	private String lang;

	private Long formId;

	private String formValue;

	private String morphCode;

	private BigDecimal weight;

	private Long memberOrder;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConjunct() {
		return conjunct;
	}

	public void setConjunct(String conjunct) {
		this.conjunct = conjunct;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getFormValue() {
		return formValue;
	}

	public void setFormValue(String formValue) {
		this.formValue = formValue;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public Long getMemberOrder() {
		return memberOrder;
	}

	public void setMemberOrder(Long memberOrder) {
		this.memberOrder = memberOrder;
	}

}
