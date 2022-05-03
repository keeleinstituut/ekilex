package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class FedTermUploadQueueContent extends AbstractDataObject implements QueueContent {

	private static final long serialVersionUID = 1L;

	private String datasetCode;

	private String datasetName;

	private int meaningOffset;

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

	public int getMeaningOffset() {
		return meaningOffset;
	}

	public void setMeaningOffset(int meaningOffset) {
		this.meaningOffset = meaningOffset;
	}

}
