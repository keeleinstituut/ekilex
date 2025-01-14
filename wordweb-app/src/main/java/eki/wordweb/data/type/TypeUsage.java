package eki.wordweb.data.type;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;
import eki.wordweb.data.ComplexityType;
import eki.wordweb.data.LangType;
import eki.wordweb.data.SourceLinkType;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TypeUsage extends AbstractDataObject implements ComplexityType, SourceLinkType, LangType {

	private static final long serialVersionUID = 1L;

	private Long usageId;

	private String value;

	private String valuePrese;

	private String lang;

	private Complexity complexity;

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
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

	@Override
	public List<TypeSourceLink> getSourceLinks() {
		return sourceLinks;
	}

	@Override
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
