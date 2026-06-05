package eki.ekilex.data;

import java.math.BigDecimal;

import eki.common.data.AbstractDataObject;

public class TermDatasetReportRow extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String datasetCode;
	private String datasetName;

	private int publicMeaningCount;
	private int allMeaningCount;
	private int publicTermCount;
	private int allTermCount;
	private int createMeaningCount;
	private int updateMeaningCount;
	private int withDomainMeaningCount;
	private int withDomainUpdateMeaningCount;
	private BigDecimal withDomainMeaningPercent;
	private BigDecimal withDomainUpdateMeaningPercent;
	private String withoutDomainTermSample;

	private int singleTermMeaningCount;
	private String singleTermMeaningTermSample;
	private int singleLangMeaningCount;
	private String singleLangMeaningTermSample;
	private int specificCharTermCount;
	private String specificCharTermSample;
	private int initialCapTermCount;
	private String initialCapTermSample;
	private int withSourceLinkTermCount;
	private int withSourceLinkMeaningUpdateTermCount;
	private int withoutSourceLinkTermCount;
	private int withoutSourceLinkMeaningUpdateTermCount;
	private String withoutSourceLinkMeaningUpdateTermSample;

	private int withDefinitionMeaningCount;
	private int withDefinitionUpdateMeaningCount;
	private String withoutDefinitionMeaningTermSample;
	private String withoutDefinitionUpdateMeaningTermSample;
	private int withPunctuationDefinitionCount;
	private String withPunctuationDefinitionTermSample;
	private int initialCapDefinitionCount;
	private String initialCapDefinitionTermSample;
	private int initialEnumerationDefinitionCount;
	private String initialEnumerationDefinitionTermSample;
	private int withSourceLinkDefinitionCount;
	private int withSourceLinkDefinitionMeaningUpdateDefinitionCount;
	private int allDefinitionCount;

	private int withUsageMeaningCount;
	private int withSourceLinkUsageCount;
	private int withSourceLinkUsageMeaningUpdateUsageCount;
	private int withoutSourceLinkUsageCount;
	private int withoutSourceLinkUsageMeaningUpdateUsageCount;
	private String withoutSourceLinkUsageTermSample;
	private String withoutSourceLinkUsageMeaningUpdateTermSample;
	private int allUsageCount;

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public int getPublicMeaningCount() {
		return publicMeaningCount;
	}

	public void setPublicMeaningCount(int publicMeaningCount) {
		this.publicMeaningCount = publicMeaningCount;
	}

	public int getAllMeaningCount() {
		return allMeaningCount;
	}

	public void setAllMeaningCount(int allMeaningCount) {
		this.allMeaningCount = allMeaningCount;
	}

	public int getPublicTermCount() {
		return publicTermCount;
	}

	public void setPublicTermCount(int publicTermCount) {
		this.publicTermCount = publicTermCount;
	}

	public int getAllTermCount() {
		return allTermCount;
	}

	public void setAllTermCount(int allTermCount) {
		this.allTermCount = allTermCount;
	}

	public int getCreateMeaningCount() {
		return createMeaningCount;
	}

	public void setCreateMeaningCount(int createMeaningCount) {
		this.createMeaningCount = createMeaningCount;
	}

	public int getUpdateMeaningCount() {
		return updateMeaningCount;
	}

	public void setUpdateMeaningCount(int updateMeaningCount) {
		this.updateMeaningCount = updateMeaningCount;
	}

	public int getWithDomainMeaningCount() {
		return withDomainMeaningCount;
	}

	public void setWithDomainMeaningCount(int withDomainMeaningCount) {
		this.withDomainMeaningCount = withDomainMeaningCount;
	}

	public int getWithDomainUpdateMeaningCount() {
		return withDomainUpdateMeaningCount;
	}

	public void setWithDomainUpdateMeaningCount(int withDomainUpdateMeaningCount) {
		this.withDomainUpdateMeaningCount = withDomainUpdateMeaningCount;
	}

	public BigDecimal getWithDomainMeaningPercent() {
		return withDomainMeaningPercent;
	}

	public void setWithDomainMeaningPercent(BigDecimal withDomainMeaningPercent) {
		this.withDomainMeaningPercent = withDomainMeaningPercent;
	}

	public BigDecimal getWithDomainUpdateMeaningPercent() {
		return withDomainUpdateMeaningPercent;
	}

	public void setWithDomainUpdateMeaningPercent(BigDecimal withDomainUpdateMeaningPercent) {
		this.withDomainUpdateMeaningPercent = withDomainUpdateMeaningPercent;
	}

	public String getWithoutDomainTermSample() {
		return withoutDomainTermSample;
	}

	public void setWithoutDomainTermSample(String withoutDomainTermSample) {
		this.withoutDomainTermSample = withoutDomainTermSample;
	}

	public int getSingleTermMeaningCount() {
		return singleTermMeaningCount;
	}

	public void setSingleTermMeaningCount(int singleTermMeaningCount) {
		this.singleTermMeaningCount = singleTermMeaningCount;
	}

	public String getSingleTermMeaningTermSample() {
		return singleTermMeaningTermSample;
	}

	public void setSingleTermMeaningTermSample(String singleTermMeaningTermSample) {
		this.singleTermMeaningTermSample = singleTermMeaningTermSample;
	}

	public int getSingleLangMeaningCount() {
		return singleLangMeaningCount;
	}

	public void setSingleLangMeaningCount(int singleLangMeaningCount) {
		this.singleLangMeaningCount = singleLangMeaningCount;
	}

	public String getSingleLangMeaningTermSample() {
		return singleLangMeaningTermSample;
	}

	public void setSingleLangMeaningTermSample(String singleLangMeaningTermSample) {
		this.singleLangMeaningTermSample = singleLangMeaningTermSample;
	}

	public int getSpecificCharTermCount() {
		return specificCharTermCount;
	}

	public void setSpecificCharTermCount(int specificCharTermCount) {
		this.specificCharTermCount = specificCharTermCount;
	}

	public String getSpecificCharTermSample() {
		return specificCharTermSample;
	}

	public void setSpecificCharTermSample(String specificCharTermSample) {
		this.specificCharTermSample = specificCharTermSample;
	}

	public int getInitialCapTermCount() {
		return initialCapTermCount;
	}

	public void setInitialCapTermCount(int initialCapTermCount) {
		this.initialCapTermCount = initialCapTermCount;
	}

	public String getInitialCapTermSample() {
		return initialCapTermSample;
	}

	public void setInitialCapTermSample(String initialCapTermSample) {
		this.initialCapTermSample = initialCapTermSample;
	}

	public int getWithSourceLinkTermCount() {
		return withSourceLinkTermCount;
	}

	public void setWithSourceLinkTermCount(int withSourceLinkTermCount) {
		this.withSourceLinkTermCount = withSourceLinkTermCount;
	}

	public int getWithSourceLinkMeaningUpdateTermCount() {
		return withSourceLinkMeaningUpdateTermCount;
	}

	public void setWithSourceLinkMeaningUpdateTermCount(int withSourceLinkMeaningUpdateTermCount) {
		this.withSourceLinkMeaningUpdateTermCount = withSourceLinkMeaningUpdateTermCount;
	}

	public int getWithoutSourceLinkTermCount() {
		return withoutSourceLinkTermCount;
	}

	public void setWithoutSourceLinkTermCount(int withoutSourceLinkTermCount) {
		this.withoutSourceLinkTermCount = withoutSourceLinkTermCount;
	}

	public int getWithoutSourceLinkMeaningUpdateTermCount() {
		return withoutSourceLinkMeaningUpdateTermCount;
	}

	public void setWithoutSourceLinkMeaningUpdateTermCount(int withoutSourceLinkMeaningUpdateTermCount) {
		this.withoutSourceLinkMeaningUpdateTermCount = withoutSourceLinkMeaningUpdateTermCount;
	}

	public String getWithoutSourceLinkMeaningUpdateTermSample() {
		return withoutSourceLinkMeaningUpdateTermSample;
	}

	public void setWithoutSourceLinkMeaningUpdateTermSample(String withoutSourceLinkMeaningUpdateTermSample) {
		this.withoutSourceLinkMeaningUpdateTermSample = withoutSourceLinkMeaningUpdateTermSample;
	}

	public int getWithDefinitionMeaningCount() {
		return withDefinitionMeaningCount;
	}

	public void setWithDefinitionMeaningCount(int withDefinitionMeaningCount) {
		this.withDefinitionMeaningCount = withDefinitionMeaningCount;
	}

	public int getWithDefinitionUpdateMeaningCount() {
		return withDefinitionUpdateMeaningCount;
	}

	public void setWithDefinitionUpdateMeaningCount(int withDefinitionUpdateMeaningCount) {
		this.withDefinitionUpdateMeaningCount = withDefinitionUpdateMeaningCount;
	}

	public String getWithoutDefinitionMeaningTermSample() {
		return withoutDefinitionMeaningTermSample;
	}

	public void setWithoutDefinitionMeaningTermSample(String withoutDefinitionMeaningTermSample) {
		this.withoutDefinitionMeaningTermSample = withoutDefinitionMeaningTermSample;
	}

	public String getWithoutDefinitionUpdateMeaningTermSample() {
		return withoutDefinitionUpdateMeaningTermSample;
	}

	public void setWithoutDefinitionUpdateMeaningTermSample(String withoutDefinitionUpdateMeaningTermSample) {
		this.withoutDefinitionUpdateMeaningTermSample = withoutDefinitionUpdateMeaningTermSample;
	}

	public int getWithPunctuationDefinitionCount() {
		return withPunctuationDefinitionCount;
	}

	public void setWithPunctuationDefinitionCount(int withPunctuationDefinitionCount) {
		this.withPunctuationDefinitionCount = withPunctuationDefinitionCount;
	}

	public String getWithPunctuationDefinitionTermSample() {
		return withPunctuationDefinitionTermSample;
	}

	public void setWithPunctuationDefinitionTermSample(String withPunctuationDefinitionTermSample) {
		this.withPunctuationDefinitionTermSample = withPunctuationDefinitionTermSample;
	}

	public int getInitialCapDefinitionCount() {
		return initialCapDefinitionCount;
	}

	public void setInitialCapDefinitionCount(int initialCapDefinitionCount) {
		this.initialCapDefinitionCount = initialCapDefinitionCount;
	}

	public String getInitialCapDefinitionTermSample() {
		return initialCapDefinitionTermSample;
	}

	public void setInitialCapDefinitionTermSample(String initialCapDefinitionTermSample) {
		this.initialCapDefinitionTermSample = initialCapDefinitionTermSample;
	}

	public int getInitialEnumerationDefinitionCount() {
		return initialEnumerationDefinitionCount;
	}

	public void setInitialEnumerationDefinitionCount(int initialEnumerationDefinitionCount) {
		this.initialEnumerationDefinitionCount = initialEnumerationDefinitionCount;
	}

	public String getInitialEnumerationDefinitionTermSample() {
		return initialEnumerationDefinitionTermSample;
	}

	public void setInitialEnumerationDefinitionTermSample(String initialEnumerationDefinitionTermSample) {
		this.initialEnumerationDefinitionTermSample = initialEnumerationDefinitionTermSample;
	}

	public int getWithSourceLinkDefinitionCount() {
		return withSourceLinkDefinitionCount;
	}

	public void setWithSourceLinkDefinitionCount(int withSourceLinkDefinitionCount) {
		this.withSourceLinkDefinitionCount = withSourceLinkDefinitionCount;
	}

	public int getWithSourceLinkDefinitionMeaningUpdateDefinitionCount() {
		return withSourceLinkDefinitionMeaningUpdateDefinitionCount;
	}

	public void setWithSourceLinkDefinitionMeaningUpdateDefinitionCount(int withSourceLinkDefinitionMeaningUpdateDefinitionCount) {
		this.withSourceLinkDefinitionMeaningUpdateDefinitionCount = withSourceLinkDefinitionMeaningUpdateDefinitionCount;
	}

	public int getAllDefinitionCount() {
		return allDefinitionCount;
	}

	public void setAllDefinitionCount(int allDefinitionCount) {
		this.allDefinitionCount = allDefinitionCount;
	}

	public int getWithUsageMeaningCount() {
		return withUsageMeaningCount;
	}

	public void setWithUsageMeaningCount(int withUsageMeaningCount) {
		this.withUsageMeaningCount = withUsageMeaningCount;
	}

	public int getWithSourceLinkUsageCount() {
		return withSourceLinkUsageCount;
	}

	public void setWithSourceLinkUsageCount(int withSourceLinkUsageCount) {
		this.withSourceLinkUsageCount = withSourceLinkUsageCount;
	}

	public int getWithSourceLinkUsageMeaningUpdateUsageCount() {
		return withSourceLinkUsageMeaningUpdateUsageCount;
	}

	public void setWithSourceLinkUsageMeaningUpdateUsageCount(int withSourceLinkUsageMeaningUpdateUsageCount) {
		this.withSourceLinkUsageMeaningUpdateUsageCount = withSourceLinkUsageMeaningUpdateUsageCount;
	}

	public int getWithoutSourceLinkUsageCount() {
		return withoutSourceLinkUsageCount;
	}

	public void setWithoutSourceLinkUsageCount(int withoutSourceLinkUsageCount) {
		this.withoutSourceLinkUsageCount = withoutSourceLinkUsageCount;
	}

	public int getWithoutSourceLinkUsageMeaningUpdateUsageCount() {
		return withoutSourceLinkUsageMeaningUpdateUsageCount;
	}

	public void setWithoutSourceLinkUsageMeaningUpdateUsageCount(int withoutSourceLinkUsageMeaningUpdateUsageCount) {
		this.withoutSourceLinkUsageMeaningUpdateUsageCount = withoutSourceLinkUsageMeaningUpdateUsageCount;
	}

	public String getWithoutSourceLinkUsageTermSample() {
		return withoutSourceLinkUsageTermSample;
	}

	public void setWithoutSourceLinkUsageTermSample(String withoutSourceLinkUsageTermSample) {
		this.withoutSourceLinkUsageTermSample = withoutSourceLinkUsageTermSample;
	}

	public String getWithoutSourceLinkUsageMeaningUpdateTermSample() {
		return withoutSourceLinkUsageMeaningUpdateTermSample;
	}

	public void setWithoutSourceLinkUsageMeaningUpdateTermSample(String withoutSourceLinkUsageMeaningUpdateTermSample) {
		this.withoutSourceLinkUsageMeaningUpdateTermSample = withoutSourceLinkUsageMeaningUpdateTermSample;
	}

	public int getAllUsageCount() {
		return allUsageCount;
	}

	public void setAllUsageCount(int allUsageCount) {
		this.allUsageCount = allUsageCount;
	}
}