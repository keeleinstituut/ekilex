package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.wordweb.constant.CollocMemberGroup;

public class DisplayColloc extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<CollocMemberGroup> memberGroupOrder;

	private CollocMember headwordMember;

	private List<CollocMember> primaryMembers;

	private List<CollocMember> contextMembers;

	private List<String> collocMemberFormValues;

	public List<CollocMemberGroup> getMemberGroupOrder() {
		return memberGroupOrder;
	}

	public void setMemberGroupOrder(List<CollocMemberGroup> memberGroupOrder) {
		this.memberGroupOrder = memberGroupOrder;
	}

	public CollocMember getHeadwordMember() {
		return headwordMember;
	}

	public void setHeadwordMember(CollocMember headwordMember) {
		this.headwordMember = headwordMember;
	}

	public List<CollocMember> getPrimaryMembers() {
		return primaryMembers;
	}

	public void setPrimaryMembers(List<CollocMember> primaryMembers) {
		this.primaryMembers = primaryMembers;
	}

	public List<CollocMember> getContextMembers() {
		return contextMembers;
	}

	public void setContextMembers(List<CollocMember> contextMembers) {
		this.contextMembers = contextMembers;
	}

	public List<String> getCollocMemberFormValues() {
		return collocMemberFormValues;
	}

	public void setCollocMemberFormValues(List<String> collocMemberFormValues) {
		this.collocMemberFormValues = collocMemberFormValues;
	}

}
