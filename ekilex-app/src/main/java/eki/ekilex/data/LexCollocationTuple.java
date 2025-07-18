package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class LexCollocationTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexCollocId;

	private Long relGroupId;

	private String memberForm;

	private String conjunct;

	private Float weight;

	private Integer memberOrder;

	private Integer groupOrder;

	private Long collocId;

	private String collocValue;

	private String collocDefinition;

	private Float collocFrequency;

	private Float collocScore;

	private List<String> collocUsages;

	public Long getLexCollocId() {
		return lexCollocId;
	}

	public void setLexCollocId(Long lexCollocId) {
		this.lexCollocId = lexCollocId;
	}

	public Long getRelGroupId() {
		return relGroupId;
	}

	public void setRelGroupId(Long relGroupId) {
		this.relGroupId = relGroupId;
	}

	public String getMemberForm() {
		return memberForm;
	}

	public void setMemberForm(String memberForm) {
		this.memberForm = memberForm;
	}

	public String getConjunct() {
		return conjunct;
	}

	public void setConjunct(String conjunct) {
		this.conjunct = conjunct;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
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

	public List<String> getCollocUsages() {
		return collocUsages;
	}

	public void setCollocUsages(List<String> collocUsages) {
		this.collocUsages = collocUsages;
	}
}
