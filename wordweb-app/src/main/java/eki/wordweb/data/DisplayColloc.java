package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.wordweb.constant.CollocMemberGroup;

public class DisplayColloc extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<CollocMemberGroup> memberGroupOrder;

	private TypeCollocMember headwordMember;

	private List<TypeCollocMember> primaryMembers;

	private List<TypeCollocMember> contextMembers;

	private List<String> collocMemberForms;

	private boolean preConjunct;

	private boolean postConjunct;

	public List<CollocMemberGroup> getMemberGroupOrder() {
		return memberGroupOrder;
	}

	public void setMemberGroupOrder(List<CollocMemberGroup> memberGroupOrder) {
		this.memberGroupOrder = memberGroupOrder;
	}

	public TypeCollocMember getHeadwordMember() {
		return headwordMember;
	}

	public void setHeadwordMember(TypeCollocMember headwordMember) {
		this.headwordMember = headwordMember;
	}

	public List<TypeCollocMember> getPrimaryMembers() {
		return primaryMembers;
	}

	public void setPrimaryMembers(List<TypeCollocMember> primaryMembers) {
		this.primaryMembers = primaryMembers;
	}

	public List<TypeCollocMember> getContextMembers() {
		return contextMembers;
	}

	public void setContextMembers(List<TypeCollocMember> contextMembers) {
		this.contextMembers = contextMembers;
	}

	public List<String> getCollocMemberForms() {
		return collocMemberForms;
	}

	public void setCollocMemberForms(List<String> collocMemberForms) {
		this.collocMemberForms = collocMemberForms;
	}

	public boolean isPreConjunct() {
		return preConjunct;
	}

	public void setPreConjunct(boolean preConjunct) {
		this.preConjunct = preConjunct;
	}

	public boolean isPostConjunct() {
		return postConjunct;
	}

	public void setPostConjunct(boolean postConjunct) {
		this.postConjunct = postConjunct;
	}

}
