package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class CollocRelGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String relGroupCode;

	private String relGroupValue;

	private List<Collocation> collocations;

	public String getRelGroupCode() {
		return relGroupCode;
	}

	public void setRelGroupCode(String relGroupCode) {
		this.relGroupCode = relGroupCode;
	}

	public String getRelGroupValue() {
		return relGroupValue;
	}

	public void setRelGroupValue(String relGroupValue) {
		this.relGroupValue = relGroupValue;
	}

	public List<Collocation> getCollocations() {
		return collocations;
	}

	public void setCollocations(List<Collocation> collocations) {
		this.collocations = collocations;
	}

}
