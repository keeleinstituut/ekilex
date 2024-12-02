package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class CollocPosGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String posGroupCode;

	private Classifier posGroup;

	private List<CollocRelGroup> relGroups;

	public String getPosGroupCode() {
		return posGroupCode;
	}

	public void setPosGroupCode(String posGroupCode) {
		this.posGroupCode = posGroupCode;
	}

	public Classifier getPosGroup() {
		return posGroup;
	}

	public void setPosGroup(Classifier posGroup) {
		this.posGroup = posGroup;
	}

	public List<CollocRelGroup> getRelGroups() {
		return relGroups;
	}

	public void setRelGroups(List<CollocRelGroup> relGroups) {
		this.relGroups = relGroups;
	}

}
