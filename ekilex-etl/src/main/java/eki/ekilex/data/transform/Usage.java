package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Usage extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String definition;

	private List<UsageTranslation> usageTranslations;

	private boolean matched;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<UsageTranslation> getUsageTranslations() {
		return usageTranslations;
	}

	public void setUsageTranslations(List<UsageTranslation> usageTranslations) {
		this.usageTranslations = usageTranslations;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

}
