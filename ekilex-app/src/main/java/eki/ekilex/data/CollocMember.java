package eki.ekilex.data;

import java.math.BigDecimal;
import java.util.List;

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

	private String morphValue;

	private String posGroupCode;

	private String posGroupValue;

	private String relGroupCode;

	private String relGroupValue;

	private BigDecimal weight;

	private Integer weightLevel;

	private Long memberOrder;

	private List<String> definitionValues;

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

	public String getMorphValue() {
		return morphValue;
	}

	public void setMorphValue(String morphValue) {
		this.morphValue = morphValue;
	}

	public String getPosGroupCode() {
		return posGroupCode;
	}

	public void setPosGroupCode(String posGroupCode) {
		this.posGroupCode = posGroupCode;
	}

	public String getPosGroupValue() {
		return posGroupValue;
	}

	public void setPosGroupValue(String posGroupValue) {
		this.posGroupValue = posGroupValue;
	}

	public String getRelGroupCode() {
		return relGroupCode;
	}

	public void setRelGroupCode(String relGroupCode) {
		this.relGroupCode = relGroupCode;
	}

	public String getRelGroupValue() {
		return relGroupValue;
	}

	public void setRelGroupValue(String relGroupValue) {
		this.relGroupValue = relGroupValue;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public Integer getWeightLevel() {
		return weightLevel;
	}

	public void setWeightLevel(Integer weightLevel) {
		this.weightLevel = weightLevel;
	}

	public Long getMemberOrder() {
		return memberOrder;
	}

	public void setMemberOrder(Long memberOrder) {
		this.memberOrder = memberOrder;
	}

	public List<String> getDefinitionValues() {
		return definitionValues;
	}

	public void setDefinitionValues(List<String> definitionValues) {
		this.definitionValues = definitionValues;
	}

}
