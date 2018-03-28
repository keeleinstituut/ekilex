package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class UsageMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long usageMeaningId;

	private Classifier usageMeaningType;

	private List<TypeUsage> usages;

	private List<String> usageTranslations;

	private List<String> usageDefinitions;

	public Long getUsageMeaningId() {
		return usageMeaningId;
	}

	public void setUsageMeaningId(Long usageMeaningId) {
		this.usageMeaningId = usageMeaningId;
	}

	public Classifier getUsageMeaningType() {
		return usageMeaningType;
	}

	public void setUsageMeaningType(Classifier usageMeaningType) {
		this.usageMeaningType = usageMeaningType;
	}

	public List<TypeUsage> getUsages() {
		return usages;
	}

	public void setUsages(List<TypeUsage> usages) {
		this.usages = usages;
	}

	public List<String> getUsageTranslations() {
		return usageTranslations;
	}

	public void setUsageTranslations(List<String> usageTranslations) {
		this.usageTranslations = usageTranslations;
	}

	public List<String> getUsageDefinitions() {
		return usageDefinitions;
	}

	public void setUsageDefinitions(List<String> usageDefinitions) {
		this.usageDefinitions = usageDefinitions;
	}

}
