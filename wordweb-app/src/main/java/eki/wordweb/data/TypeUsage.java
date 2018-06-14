package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class TypeUsage extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String usage;

	private String usageLang;

	private String usageTypeCode;

	private Classifier usageType;

	private List<String> usageTranslations;

	private List<String> usageDefinitions;

	private List<TypeSourceLink> usageAuthors;

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getUsageLang() {
		return usageLang;
	}

	public void setUsageLang(String usageLang) {
		this.usageLang = usageLang;
	}

	public String getUsageTypeCode() {
		return usageTypeCode;
	}

	public void setUsageTypeCode(String usageTypeCode) {
		this.usageTypeCode = usageTypeCode;
	}

	public Classifier getUsageType() {
		return usageType;
	}

	public void setUsageType(Classifier usageType) {
		this.usageType = usageType;
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

	public List<TypeSourceLink> getUsageAuthors() {
		return usageAuthors;
	}

	public void setUsageAuthors(List<TypeSourceLink> usageAuthors) {
		this.usageAuthors = usageAuthors;
	}

}
