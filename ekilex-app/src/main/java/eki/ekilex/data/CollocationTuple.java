package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class CollocationTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long posGroupId;

	private String posGroupName;

	private Long relGroupId;

	private String relGroupName;

	private Float relGroupFrequency;

	private Float relGroupScore;

	private Long collocId;

	private String collocValue;

	private Float collocFrequency;

	private Float collocScore;

	private List<String> collocUsages;

	private Long collocWordId;

	private String collocWord;

	public Long getPosGroupId() {
		return posGroupId;
	}

	public void setPosGroupId(Long posGroupId) {
		this.posGroupId = posGroupId;
	}

	public String getPosGroupName() {
		return posGroupName;
	}

	public void setPosGroupName(String posGroupName) {
		this.posGroupName = posGroupName;
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

	public Long getCollocWordId() {
		return collocWordId;
	}

	public void setCollocWordId(Long collocWordId) {
		this.collocWordId = collocWordId;
	}

	public String getCollocWord() {
		return collocWord;
	}

	public void setCollocWord(String collocWord) {
		this.collocWord = collocWord;
	}

}
