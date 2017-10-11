package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Usage extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private List<UsageTranslation> usageTranslations;

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

}
