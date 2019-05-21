package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class LexemeData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String processStateCode;

	private String datasetCode;

	public String getProcessStateCode() {
		return processStateCode;
	}

	public void setProcessStateCode(String processStateCode) {
		this.processStateCode = processStateCode;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}
}
