package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class Tag extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String name;

	private boolean setAutomatically;

	private boolean removeToComplete;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSetAutomatically() {
		return setAutomatically;
	}

	public void setSetAutomatically(boolean setAutomatically) {
		this.setAutomatically = setAutomatically;
	}

	public boolean isRemoveToComplete() {
		return removeToComplete;
	}

	public void setRemoveToComplete(boolean removeToComplete) {
		this.removeToComplete = removeToComplete;
	}
}
