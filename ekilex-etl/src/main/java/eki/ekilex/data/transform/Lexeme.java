package eki.ekilex.data.transform;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class Lexeme extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long wordId;

	private Long meaningId;

	private String datasetCode;

	private String frequencyGroupCode;

	private Float corpusFrequency;

	private Integer level1;

	private Integer level2;

	private Integer level3;

	private String valueStateCode;

	private String processStateCode;

	private Complexity complexity;

	private Long orderBy;

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

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getFrequencyGroupCode() {
		return frequencyGroupCode;
	}

	public void setFrequencyGroupCode(String frequencyGroupCode) {
		this.frequencyGroupCode = frequencyGroupCode;
	}

	public Float getCorpusFrequency() {
		return corpusFrequency;
	}

	public void setCorpusFrequency(Float corpusFrequency) {
		this.corpusFrequency = corpusFrequency;
	}

	public Integer getLevel1() {
		return level1;
	}

	public void setLevel1(Integer level1) {
		this.level1 = level1;
	}

	public Integer getLevel2() {
		return level2;
	}

	public void setLevel2(Integer level2) {
		this.level2 = level2;
	}

	public Integer getLevel3() {
		return level3;
	}

	public void setLevel3(Integer level3) {
		this.level3 = level3;
	}

	public String getValueStateCode() {
		return valueStateCode;
	}

	public void setValueStateCode(String valueStateCode) {
		this.valueStateCode = valueStateCode;
	}

	public String getProcessStateCode() {
		return processStateCode;
	}

	public void setProcessStateCode(String processStateCode) {
		this.processStateCode = processStateCode;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

}
