package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Collocation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long wordId;

	private String wordValue;

	private List<Usage> usages;

	private List<CollocMember> members;

	private Long groupOrder;

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

	public List<Usage> getUsages() {
		return usages;
	}

	public void setUsages(List<Usage> usages) {
		this.usages = usages;
	}

	public List<CollocMember> getMembers() {
		return members;
	}

	public void setMembers(List<CollocMember> members) {
		this.members = members;
	}

	public Long getGroupOrder() {
		return groupOrder;
	}

	public void setGroupOrder(Long groupOrder) {
		this.groupOrder = groupOrder;
	}

}
