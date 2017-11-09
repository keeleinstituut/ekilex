package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class UsageMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private List<UsageMember> usages;

	private List<UsageMember> usageTranslations;

	private List<UsageMember> usageDefinitions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<UsageMember> getUsages() {
		return usages;
	}

	public void setUsages(List<UsageMember> usages) {
		this.usages = usages;
	}

	public List<UsageMember> getUsageTranslations() {
		return usageTranslations;
	}

	public void setUsageTranslations(List<UsageMember> usageTranslations) {
		this.usageTranslations = usageTranslations;
	}

	public List<UsageMember> getUsageDefinitions() {
		return usageDefinitions;
	}

	public void setUsageDefinitions(List<UsageMember> usageDefinitions) {
		this.usageDefinitions = usageDefinitions;
	}

}
