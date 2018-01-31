package eki.ekilex.data;

import java.util.function.Consumer;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class GovernmentUsageTranslationDefinitionTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "government_id")
	private Long governmentId;

	@Column(name = "government_value")
	private String governmentValue;

	@Column(name = "usage_meaning_id")
	private Long usageMeaningId;

	@Column(name = "usage_id")
	private Long usageId;

	@Column(name = "usage_value")
	private String usageValue;

	@Column(name = "usage_lang")
	private String usageLang;

	@Column(name = "usage_translation_id")
	private Long usageTranslationId;

	@Column(name = "usage_translation_value")
	private String usageTranslationValue;

	@Column(name = "usage_translation_lang")
	private String usageTranslationLang;

	@Column(name = "usage_definition_id")
	private Long usageDefinitionId;

	@Column(name = "usage_definition_value")
	private String usageDefinitionValue;

	@Column(name = "usage_definition_lang")
	private String usageDefinitionLang;

	@Column(name = "usage_author")
	private String usageAuthor;

	@Column(name = "usage_author_type")
	private String usageAuthorType;

	@Column(name = "usage_type")
	private String usageType;

	public GovernmentUsageTranslationDefinitionTuple() {
	}

	public GovernmentUsageTranslationDefinitionTuple(Consumer<GovernmentUsageTranslationDefinitionTuple> builder) {
		builder.accept(this);
	}

	public Long getGovernmentId() {
		return governmentId;
	}

	public void setGovernmentId(Long governmentId) {
		this.governmentId = governmentId;
	}

	public String getGovernmentValue() {
		return governmentValue;
	}

	public void setGovernmentValue(String governmentValue) {
		this.governmentValue = governmentValue;
	}

	public Long getUsageMeaningId() {
		return usageMeaningId;
	}

	public void setUsageMeaningId(Long usageMeaningId) {
		this.usageMeaningId = usageMeaningId;
	}

	public Long getUsageId() {
		return usageId;
	}

	public void setUsageId(Long usageId) {
		this.usageId = usageId;
	}

	public String getUsageValue() {
		return usageValue;
	}

	public void setUsageValue(String usageValue) {
		this.usageValue = usageValue;
	}

	public String getUsageLang() {
		return usageLang;
	}

	public void setUsageLang(String usageLang) {
		this.usageLang = usageLang;
	}

	public Long getUsageTranslationId() {
		return usageTranslationId;
	}

	public void setUsageTranslationId(Long usageTranslationId) {
		this.usageTranslationId = usageTranslationId;
	}

	public String getUsageTranslationValue() {
		return usageTranslationValue;
	}

	public void setUsageTranslationValue(String usageTranslationValue) {
		this.usageTranslationValue = usageTranslationValue;
	}

	public String getUsageTranslationLang() {
		return usageTranslationLang;
	}

	public void setUsageTranslationLang(String usageTranslationLang) {
		this.usageTranslationLang = usageTranslationLang;
	}

	public Long getUsageDefinitionId() {
		return usageDefinitionId;
	}

	public void setUsageDefinitionId(Long usageDefinitionId) {
		this.usageDefinitionId = usageDefinitionId;
	}

	public String getUsageDefinitionValue() {
		return usageDefinitionValue;
	}

	public void setUsageDefinitionValue(String usageDefinitionValue) {
		this.usageDefinitionValue = usageDefinitionValue;
	}

	public String getUsageDefinitionLang() {
		return usageDefinitionLang;
	}

	public void setUsageDefinitionLang(String usageDefinitionLang) {
		this.usageDefinitionLang = usageDefinitionLang;
	}

	public String getUsageAuthor() {
		return usageAuthor;
	}

	public void setUsageAuthor(String usageAuthor) {
		this.usageAuthor = usageAuthor;
	}

	public String getUsageAuthorType() {
		return usageAuthorType;
	}

	public void setUsageAuthorType(String usageAuthorType) {
		this.usageAuthorType = usageAuthorType;
	}

	public String getUsageType() {
		return usageType;
	}

	public void setUsageType(String usageType) {
		this.usageType = usageType;
	}
}
