package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class ClassifierFull extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String name;

	private String code;

	private boolean hasLabel;

	private List<ClassifierLabel> labels;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isHasLabel() {
		return hasLabel;
	}

	public void setHasLabel(boolean hasLabel) {
		this.hasLabel = hasLabel;
	}

	public List<ClassifierLabel> getLabels() {
		return labels;
	}

	public void setLabels(List<ClassifierLabel> labels) {
		this.labels = labels;
	}
}
