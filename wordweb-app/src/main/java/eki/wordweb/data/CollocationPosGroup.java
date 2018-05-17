package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class CollocationPosGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String name;

	private List<CollocationRelGroup> relationGroups;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CollocationRelGroup> getRelationGroups() {
		return relationGroups;
	}

	public void setRelationGroups(List<CollocationRelGroup> relationGroups) {
		this.relationGroups = relationGroups;
	}

}
