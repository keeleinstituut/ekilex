package eki.ekilex.data;

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
}