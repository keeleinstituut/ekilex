package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class CollocationTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long wordId;

	private Long posGroupId;

	private String posGroupCode;

	private Long relGroupId;

	private String relGroupName;

	private Long collocId;

	private String collocValue;

	private String collocDefinition;

	private List<String> collocUsages;

	private List<TypeCollocMember> collocMembers;

	private boolean invalid;

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

	public List<TypeCollocMember> getCollocMembers() {
		return collocMembers;
	}

	public void setCollocMembers(List<TypeCollocMember> collocMembers) {
		this.collocMembers = collocMembers;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

}
