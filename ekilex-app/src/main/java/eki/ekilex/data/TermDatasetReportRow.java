package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

import java.math.BigDecimal;

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
}