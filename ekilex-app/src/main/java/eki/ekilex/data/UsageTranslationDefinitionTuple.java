package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class UsageTranslationDefinitionTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "usage_id")
	private Long usageId;

	@Column(name = "usage_value")
	private String usageValue;

	@Column(name = "usage_lang")
	private String usageLang;

	@Column(name = "usage_type_code")
	private String usageTypeCode;

	@Column(name = "usage_type_value")
	private String usageTypeValue;

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

	@Column(name = "usage_translator")
	private String usageTranslator;

	@Column(name = "usage_source_ref_link_id")
	private Long usageSourceRefLinkId;

	@Column(name = "usage_source_ref_link_name")
	private String usageSourceRefLinkName;

	@Column(name = "usage_source_ref_link_value")
	private String usageSourceRefLinkValue;

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

	public String getUsageTypeCode() {
		return usageTypeCode;
	}

	public void setUsageTypeCode(String usageTypeCode) {
		this.usageTypeCode = usageTypeCode;
	}

	public String getUsageTypeValue() {
		return usageTypeValue;
	}

	public void setUsageTypeValue(String usageTypeValue) {
		this.usageTypeValue = usageTypeValue;
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

	public String getUsageTranslator() {
		return usageTranslator;
	}

	public void setUsageTranslator(String usageTranslator) {
		this.usageTranslator = usageTranslator;
	}

	public Long getUsageSourceRefLinkId() {
		return usageSourceRefLinkId;
	}

	public void setUsageSourceRefLinkId(Long usageSourceRefLinkId) {
		this.usageSourceRefLinkId = usageSourceRefLinkId;
	}

	public String getUsageSourceRefLinkName() {
		return usageSourceRefLinkName;
	}

	public void setUsageSourceRefLinkName(String usageSourceRefLinkName) {
		this.usageSourceRefLinkName = usageSourceRefLinkName;
	}

	public String getUsageSourceRefLinkValue() {
		return usageSourceRefLinkValue;
	}

	public void setUsageSourceRefLinkValue(String usageSourceRefLinkValue) {
		this.usageSourceRefLinkValue = usageSourceRefLinkValue;
	}

}
