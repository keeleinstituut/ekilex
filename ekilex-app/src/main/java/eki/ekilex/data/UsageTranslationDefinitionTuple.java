package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.constant.Complexity;
import eki.common.constant.ReferenceType;
import eki.common.constant.SourceType;
import eki.common.data.AbstractDataObject;

public class UsageTranslationDefinitionTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "usage_id")
	private Long usageId;

	@Column(name = "usage_value")
	private String usageValue;

	@Column(name = "usage_lang")
	private String usageLang;

	@Column(name = "usage_complexity")
	private Complexity usageComplexity;

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

	@Column(name = "usage_source_link_id")
	private Long usageSourceLinkId;

	@Column(name = "usage_source_link_type")
	private ReferenceType usageSourceLinkType;

	@Column(name = "usage_source_link_name")
	private String usageSourceLinkName;

	@Column(name = "usage_source_link_value")
	private String usageSourceLinkValue;

	@Column(name = "usage_source_id")
	private Long usageSourceId;

	@Column(name = "usage_source_type")
	private SourceType usageSourceType;

	@Column(name = "usage_source_name")
	private String usageSourceName;

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

	public Complexity getUsageComplexity() {
		return usageComplexity;
	}

	public void setUsageComplexity(Complexity usageComplexity) {
		this.usageComplexity = usageComplexity;
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

	public Long getUsageSourceLinkId() {
		return usageSourceLinkId;
	}

	public void setUsageSourceLinkId(Long usageSourceLinkId) {
		this.usageSourceLinkId = usageSourceLinkId;
	}

	public ReferenceType getUsageSourceLinkType() {
		return usageSourceLinkType;
	}

	public void setUsageSourceLinkType(ReferenceType usageSourceLinkType) {
		this.usageSourceLinkType = usageSourceLinkType;
	}

	public String getUsageSourceLinkName() {
		return usageSourceLinkName;
	}

	public void setUsageSourceLinkName(String usageSourceLinkName) {
		this.usageSourceLinkName = usageSourceLinkName;
	}

	public String getUsageSourceLinkValue() {
		return usageSourceLinkValue;
	}

	public void setUsageSourceLinkValue(String usageSourceLinkValue) {
		this.usageSourceLinkValue = usageSourceLinkValue;
	}

	public Long getUsageSourceId() {
		return usageSourceId;
	}

	public void setUsageSourceId(Long usageSourceId) {
		this.usageSourceId = usageSourceId;
	}

	public SourceType getUsageSourceType() {
		return usageSourceType;
	}

	public void setUsageSourceType(SourceType usageSourceType) {
		this.usageSourceType = usageSourceType;
	}

	public String getUsageSourceName() {
		return usageSourceName;
	}

	public void setUsageSourceName(String usageSourceName) {
		this.usageSourceName = usageSourceName;
	}

}
