package eki.ekilex.data.transform;

import java.util.ArrayList;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class UsageMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Usage> usages;

	private List<UsageTranslation> usageTranslations;

	private List<String> definitions;

	public UsageMeaning() {
		this.usages = new ArrayList<>();
		this.usageTranslations = new ArrayList<>();
		this.definitions = new ArrayList<>();
	}

	public List<Usage> getUsages() {
		return usages;
	}

	public void setUsages(List<Usage> usages) {
		this.usages = usages;
	}

	public List<UsageTranslation> getUsageTranslations() {
		return usageTranslations;
	}

	public void setUsageTranslations(List<UsageTranslation> usageTranslations) {
		this.usageTranslations = usageTranslations;
	}

	public List<String> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
	}
}
