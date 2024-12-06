package eki.wordweb.data;

import java.math.BigDecimal;

import eki.common.data.AbstractDataObject;

public class CollocMember extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

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

	private boolean headword;

	private boolean primary;

	private boolean context;

	private boolean preConjunct;

	private boolean postConjunct;

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

	public boolean isHeadword() {
		return headword;
	}

	public void setHeadword(boolean headword) {
		this.headword = headword;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public boolean isContext() {
		return context;
	}

	public void setContext(boolean context) {
		this.context = context;
	}

	public boolean isPreConjunct() {
		return preConjunct;
	}

	public void setPreConjunct(boolean preConjunct) {
		this.preConjunct = preConjunct;
	}

	public boolean isPostConjunct() {
		return postConjunct;
	}

	public void setPostConjunct(boolean postConjunct) {
		this.postConjunct = postConjunct;
	}

}
