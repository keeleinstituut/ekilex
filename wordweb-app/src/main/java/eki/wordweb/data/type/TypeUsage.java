package eki.wordweb.data.type;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;
import eki.wordweb.data.ComplexityType;
import eki.wordweb.data.LangType;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TypeUsage extends AbstractDataObject implements ComplexityType, LangType {

	private static final long serialVersionUID = 1L;

	private Long usageId;

	private String usage;

	private String usagePrese;

	private String usageLang;

	private Complexity complexity;

	private String usageTypeCode;

	private Classifier usageType;

	private List<String> usageTranslations;

	private List<String> usageDefinitions;

	private List<TypeSourceLink> sourceLinks;

	private boolean putOnSpeaker;

	public Long getUsageId() {
		return usageId;
	}

	public void setUsageId(Long usageId) {
		this.usageId = usageId;
	}

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

	@Override
	public String getLang() {
		return usageLang;
	}

	public String getUsageLang() {
		return usageLang;
	}

	public void setUsageLang(String usageLang) {
		this.usageLang = usageLang;
	}

	@Override
	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
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

	public List<TypeSourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<TypeSourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

	public boolean isPutOnSpeaker() {
		return putOnSpeaker;
	}

	public void setPutOnSpeaker(boolean putOnSpeaker) {
		this.putOnSpeaker = putOnSpeaker;
	}

}
