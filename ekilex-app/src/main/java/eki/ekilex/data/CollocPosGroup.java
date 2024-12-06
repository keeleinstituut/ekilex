package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class CollocPosGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String posGroupCode;

	private String posGroupValue;

	private List<CollocRelGroup> relGroups;

	public String getPosGroupCode() {
		return posGroupCode;
	}

	public void setPosGroupCode(String posGroupCode) {
		this.posGroupCode = posGroupCode;
	}

	public String getPosGroupValue() {
		return posGroupValue;
	}

	public void setPosGroupValue(String posGroupValue) {
		this.posGroupValue = posGroupValue;
	}

	public List<CollocRelGroup> getRelGroups() {
		return relGroups;
	}

	public void setRelGroups(List<CollocRelGroup> relGroups) {
		this.relGroups = relGroups;
	}

}
