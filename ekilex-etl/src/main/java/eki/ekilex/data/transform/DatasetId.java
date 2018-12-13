package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class DatasetId extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String dataset;

	private Integer id;

	public DatasetId(String dataset, Integer id) {
		this.dataset = dataset;
		this.id = id;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
