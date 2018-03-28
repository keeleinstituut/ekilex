package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class LexemeDetailsTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private List<String> adviceNotes;

	private List<String> publicNotes;

	private List<String> grammars;

	private Long governmentId;

	private String government;

	private Long usageMeaningId;

	private String usageMeaningTypeCode;

	private List<TypeUsage> usages;

	private List<String> usageTranslations;

	private List<String> usageDefinitions;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public List<String> getAdviceNotes() {
		return adviceNotes;
	}

	public void setAdviceNotes(List<String> adviceNotes) {
		this.adviceNotes = adviceNotes;
	}

	public List<String> getPublicNotes() {
		return publicNotes;
	}

	public void setPublicNotes(List<String> publicNotes) {
		this.publicNotes = publicNotes;
	}

	public List<String> getGrammars() {
		return grammars;
	}

	public void setGrammars(List<String> grammars) {
		this.grammars = grammars;
	}

	public Long getGovernmentId() {
		return governmentId;
	}

	public void setGovernmentId(Long governmentId) {
		this.governmentId = governmentId;
	}

	public String getGovernment() {
		return government;
	}

	public void setGovernment(String government) {
		this.government = government;
	}

	public Long getUsageMeaningId() {
		return usageMeaningId;
	}

	public void setUsageMeaningId(Long usageMeaningId) {
		this.usageMeaningId = usageMeaningId;
	}

	public String getUsageMeaningTypeCode() {
		return usageMeaningTypeCode;
	}

	public void setUsageMeaningTypeCode(String usageMeaningTypeCode) {
		this.usageMeaningTypeCode = usageMeaningTypeCode;
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
