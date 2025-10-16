package eki.ekilex.data.migra;

import java.util.List;

import eki.ekilex.data.AbstractPublishingEntity;

public class Colloc extends AbstractPublishingEntity {

	private static final long serialVersionUID = 1L;

	private Long collocLexemeId;

	private Long collocWordId;

	private String collocWordValue;

	private List<String> usageValues;

	private List<CollocMember> collocMembers;

	private String hash;

	private List<String> collocMemberHashes;

	private int collocMemberCount;

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

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public List<String> getCollocMemberHashes() {
		return collocMemberHashes;
	}

	public void setCollocMemberHashes(List<String> collocMemberHashes) {
		this.collocMemberHashes = collocMemberHashes;
	}

	public int getCollocMemberCount() {
		return collocMemberCount;
	}

	public void setCollocMemberCount(int collocMemberCount) {
		this.collocMemberCount = collocMemberCount;
	}

}
