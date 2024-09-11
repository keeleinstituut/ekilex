package eki.ekilex.data;

import eki.common.constant.FreeformOwner;
import eki.common.data.AbstractDataObject;

public class DatasetFreeformType extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String datasetCode;

	private FreeformOwner freeformOwner;

	private String freeformTypeCode;

	private Classifier freeformType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public FreeformOwner getFreeformOwner() {
		return freeformOwner;
	}

	public void setFreeformOwner(FreeformOwner freeformOwner) {
		this.freeformOwner = freeformOwner;
	}

	public String getFreeformTypeCode() {
		return freeformTypeCode;
	}

	public void setFreeformTypeCode(String freeformTypeCode) {
		this.freeformTypeCode = freeformTypeCode;
	}

	public Classifier getFreeformType() {
		return freeformType;
	}

	public void setFreeformType(Classifier freeformType) {
		this.freeformType = freeformType;
	}

}
