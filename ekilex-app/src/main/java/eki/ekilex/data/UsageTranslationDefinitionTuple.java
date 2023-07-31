package eki.ekilex.data;

import eki.common.constant.Complexity;
import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class UsageTranslationDefinitionTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long usageId;

	private String usageValue;

	private String usageLang;

	private Complexity usageComplexity;

	private Long usageOrderBy;

	private String usageTypeCode;

	private String usageTypeValue;

	private Long usageTranslationId;

	private String usageTranslationValue;

	private String usageTranslationLang;

	private Long usageDefinitionId;

	private String usageDefinitionValue;

	private String usageDefinitionLang;

	private Long usageSourceLinkId;

	private ReferenceType usageSourceLinkType;

	private String usageSourceLinkName;

	private String usageSourceLinkValue;

	private boolean isUsagePublic;

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

	public Long getUsageOrderBy() {
		return usageOrderBy;
	}

	public void setUsageOrderBy(Long usageOrderBy) {
		this.usageOrderBy = usageOrderBy;
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

	public boolean isUsagePublic() {
		return isUsagePublic;
	}

	public void setUsagePublic(boolean usagePublic) {
		isUsagePublic = usagePublic;
	}
}
