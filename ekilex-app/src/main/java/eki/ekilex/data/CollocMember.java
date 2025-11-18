package eki.ekilex.data;

import java.math.BigDecimal;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class CollocMember extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long collocLexemeId;

	private Long memberLexemeId;

	private Long memberWordId;

	private String memberWordValue;

	private Integer homonymNr;

	private String lang;

	private Long memberFormId;

	private String memberFormValue;

	private String morphCode;

	private String morphValue;

	private Long conjunctLexemeId;

	private String conjunctValue;

	private String posGroupCode;

	private String posGroupValue;

	private String relGroupCode;

	private String relGroupValue;

	private BigDecimal weight;

	private Integer weightLevel;

	private Integer memberOrder;

	private Integer groupOrder;

	private List<String> definitionValues;

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

	public Long getMemberWordId() {
		return memberWordId;
	}

	public void setMemberWordId(Long memberWordId) {
		this.memberWordId = memberWordId;
	}

	public String getMemberWordValue() {
		return memberWordValue;
	}

	public void setMemberWordValue(String memberWordValue) {
		this.memberWordValue = memberWordValue;
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

	public Long getMemberFormId() {
		return memberFormId;
	}

	public void setMemberFormId(Long memberFormId) {
		this.memberFormId = memberFormId;
	}

	public String getMemberFormValue() {
		return memberFormValue;
	}

	public void setMemberFormValue(String memberFormValue) {
		this.memberFormValue = memberFormValue;
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

	public Long getConjunctLexemeId() {
		return conjunctLexemeId;
	}

	public void setConjunctLexemeId(Long conjunctLexemeId) {
		this.conjunctLexemeId = conjunctLexemeId;
	}

	public String getConjunctValue() {
		return conjunctValue;
	}

	public void setConjunctValue(String conjunctValue) {
		this.conjunctValue = conjunctValue;
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

	public List<String> getDefinitionValues() {
		return definitionValues;
	}

	public void setDefinitionValues(List<String> definitionValues) {
		this.definitionValues = definitionValues;
	}

}
