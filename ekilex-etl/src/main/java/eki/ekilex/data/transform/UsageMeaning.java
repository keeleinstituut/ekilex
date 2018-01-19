package eki.ekilex.data.transform;

import java.util.ArrayList;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class UsageMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Usage> usages = new ArrayList<>();

	private List<String> definitions = new ArrayList<>();

	public List<Usage> getUsages() {
		return usages;
	}

	public void setUsages(List<Usage> usages) {
		this.usages = usages;
	}

	public List<String> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
	}
}
