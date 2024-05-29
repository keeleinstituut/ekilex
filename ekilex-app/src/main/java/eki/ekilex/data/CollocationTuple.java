package eki.ekilex.data;

import java.math.BigDecimal;
import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class CollocationTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long collocId;

	private String collocValue;

	private String collocDefinition;

	private List<String> collocUsages;

	private Float collocFrequency;

	private Float collocScore;

	private Complexity complexity;

	private Long posGroupId;

	private String posGroupCode;

	private Long relGroupId;

	private String relGroupName;

	private Float relGroupFrequency;

	private Float relGroupScore;

	private Long collocMemberLexemeId;

	private Long collocMemberWordId;

	private String collocMemberWordValue;

	private Long collocMemberFormId;

	private String collocMemberFormValue;

	private String collocMemberMorphCode;

	private String collocMemberConjunct;

	private BigDecimal collocMemberWeight;

	private Integer collocGroupOrder;

	private Integer collocMemberOrder;

	public Long getCollocId() {
		return collocId;
	}

	public void setCollocId(Long collocId) {
		this.collocId = collocId;
	}

	public String getCollocValue() {
		return collocValue;
	}

	public void setCollocValue(String collocValue) {
		this.collocValue = collocValue;
	}

	public String getCollocDefinition() {
		return collocDefinition;
	}

	public void setCollocDefinition(String collocDefinition) {
		this.collocDefinition = collocDefinition;
	}

	public List<String> getCollocUsages() {
		return collocUsages;
	}

	public void setCollocUsages(List<String> collocUsages) {
		this.collocUsages = collocUsages;
	}

	public Float getCollocFrequency() {
		return collocFrequency;
	}

	public void setCollocFrequency(Float collocFrequency) {
		this.collocFrequency = collocFrequency;
	}

	public Float getCollocScore() {
		return collocScore;
	}

	public void setCollocScore(Float collocScore) {
		this.collocScore = collocScore;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
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

	public Long getCollocMemberLexemeId() {
		return collocMemberLexemeId;
	}

	public void setCollocMemberLexemeId(Long collocMemberLexemeId) {
		this.collocMemberLexemeId = collocMemberLexemeId;
	}

	public Long getCollocMemberWordId() {
		return collocMemberWordId;
	}

	public void setCollocMemberWordId(Long collocMemberWordId) {
		this.collocMemberWordId = collocMemberWordId;
	}

	public String getCollocMemberWordValue() {
		return collocMemberWordValue;
	}

	public void setCollocMemberWordValue(String collocMemberWordValue) {
		this.collocMemberWordValue = collocMemberWordValue;
	}

	public Long getCollocMemberFormId() {
		return collocMemberFormId;
	}

	public void setCollocMemberFormId(Long collocMemberFormId) {
		this.collocMemberFormId = collocMemberFormId;
	}

	public String getCollocMemberFormValue() {
		return collocMemberFormValue;
	}

	public void setCollocMemberFormValue(String collocMemberFormValue) {
		this.collocMemberFormValue = collocMemberFormValue;
	}

	public String getCollocMemberMorphCode() {
		return collocMemberMorphCode;
	}

	public void setCollocMemberMorphCode(String collocMemberMorphCode) {
		this.collocMemberMorphCode = collocMemberMorphCode;
	}

	public String getCollocMemberConjunct() {
		return collocMemberConjunct;
	}

	public void setCollocMemberConjunct(String collocMemberConjunct) {
		this.collocMemberConjunct = collocMemberConjunct;
	}

	public BigDecimal getCollocMemberWeight() {
		return collocMemberWeight;
	}

	public void setCollocMemberWeight(BigDecimal collocMemberWeight) {
		this.collocMemberWeight = collocMemberWeight;
	}

	public Integer getCollocGroupOrder() {
		return collocGroupOrder;
	}

	public void setCollocGroupOrder(Integer collocGroupOrder) {
		this.collocGroupOrder = collocGroupOrder;
	}

	public Integer getCollocMemberOrder() {
		return collocMemberOrder;
	}

	public void setCollocMemberOrder(Integer collocMemberOrder) {
		this.collocMemberOrder = collocMemberOrder;
	}

}
