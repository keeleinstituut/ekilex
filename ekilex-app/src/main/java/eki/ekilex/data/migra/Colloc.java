package eki.ekilex.data.migra;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Colloc extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long collocLexemeId;

	private Long collocWordId;

	private String collocWordValue;

	private List<String> usageValues;

	private List<CollocMember> collocMembers;

	private String collocHash;

	private String membersHash;

	public Long getCollocLexemeId() {
		return collocLexemeId;
	}

	public void setCollocLexemeId(Long collocLexemeId) {
		this.collocLexemeId = collocLexemeId;
	}

	public Long getCollocWordId() {
		return collocWordId;
	}

	public void setCollocWordId(Long collocWordId) {
		this.collocWordId = collocWordId;
	}

	public String getCollocWordValue() {
		return collocWordValue;
	}

	public void setCollocWordValue(String collocWordValue) {
		this.collocWordValue = collocWordValue;
	}

	public List<String> getUsageValues() {
		return usageValues;
	}

	public void setUsageValues(List<String> usageValues) {
		this.usageValues = usageValues;
	}

	public List<CollocMember> getCollocMembers() {
		return collocMembers;
	}

	public void setCollocMembers(List<CollocMember> collocMembers) {
		this.collocMembers = collocMembers;
	}

	public String getCollocHash() {
		return collocHash;
	}

	public void setCollocHash(String collocHash) {
		this.collocHash = collocHash;
	}

	public String getMembersHash() {
		return membersHash;
	}

	public void setMembersHash(String membersHash) {
		this.membersHash = membersHash;
	}

}
