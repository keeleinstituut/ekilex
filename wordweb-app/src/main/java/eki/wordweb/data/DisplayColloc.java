package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class DisplayColloc extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> preContext;

	private List<String> postContext;

	private List<TypeCollocMember> collocMembers;

	public List<String> getPreContext() {
		return preContext;
	}

	public void setPreContext(List<String> preContext) {
		this.preContext = preContext;
	}

	public List<String> getPostContext() {
		return postContext;
	}

	public void setPostContext(List<String> postContext) {
		this.postContext = postContext;
	}

	public List<TypeCollocMember> getCollocMembers() {
		return collocMembers;
	}

	public void setCollocMembers(List<TypeCollocMember> collocMembers) {
		this.collocMembers = collocMembers;
	}

}
