package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class FedTermUploadQueueContent extends AbstractDataObject implements QueueContent {

	private static final long serialVersionUID = 1L;

	private String datasetCode;

	private int meaningOffset;

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public int getMeaningOffset() {
		return meaningOffset;
	}

	public void setMeaningOffset(int meaningOffset) {
		this.meaningOffset = meaningOffset;
	}

}
