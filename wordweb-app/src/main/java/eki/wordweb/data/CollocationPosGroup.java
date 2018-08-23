package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class CollocationPosGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long posGroupId;

	private Classifier posGroup;

	private List<CollocationRelGroup> relationGroups;

	public Long getPosGroupId() {
		return posGroupId;
	}

	public void setPosGroupId(Long posGroupId) {
		this.posGroupId = posGroupId;
	}

	public Classifier getPosGroup() {
		return posGroup;
	}

	public void setPosGroup(Classifier posGroup) {
		this.posGroup = posGroup;
	}

	public List<CollocationRelGroup> getRelationGroups() {
		return relationGroups;
	}

	public void setRelationGroups(List<CollocationRelGroup> relationGroups) {
		this.relationGroups = relationGroups;
	}

}
