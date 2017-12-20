package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class UsageMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Usage> usages;

	public List<Usage> getUsages() {
		return usages;
	}

	public void setUsages(List<Usage> usages) {
		this.usages = usages;
	}
}
