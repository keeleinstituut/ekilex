package eki.wordweb.data;

import java.util.List;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class TypeUsage extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "usage")
	private String usage;

	@Column(name = "usage_prese")
	private String usagePrese;

	@Column(name = "usage_lang")
	private String usageLang;

	@Column(name = "usage_type_code")
	private String usageTypeCode;

	private Classifier usageType;

	@Column(name = "usage_translations")
	private List<String> usageTranslations;

	@Column(name = "usage_definitions")
	private List<String> usageDefinitions;

	@Column(name = "usage_authors")
	private List<String> usageAuthorsRaw;

	private List<SourceLink> usageAuthors;

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getUsagePrese() {
		return usagePrese;
	}

	public void setUsagePrese(String usagePrese) {
		this.usagePrese = usagePrese;
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

	public List<String> getUsageAuthorsRaw() {
		return usageAuthorsRaw;
	}

	public void setUsageAuthorsRaw(List<String> usageAuthorsRaw) {
		this.usageAuthorsRaw = usageAuthorsRaw;
	}

	public List<SourceLink> getUsageAuthors() {
		return usageAuthors;
	}

	public void setUsageAuthors(List<SourceLink> usageAuthors) {
		this.usageAuthors = usageAuthors;
	}

}
