package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Tag extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long order;

	private String name;

	private String type;

	private boolean setAutomatically;

	private boolean removeToComplete;

	private boolean used;

	private List<String> datasetCodes;

	private String detailSearchUri;

	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSetAutomatically() {
		return setAutomatically;
	}

	public void setSetAutomatically(boolean setAutomatically) {
		this.setAutomatically = setAutomatically;
	}

	public boolean isRemoveToComplete() {
		return removeToComplete;
	}

	public void setRemoveToComplete(boolean removeToComplete) {
		this.removeToComplete = removeToComplete;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

	public String getDetailSearchUri() {
		return detailSearchUri;
	}

	public void setDetailSearchUri(String detailSearchUri) {
		this.detailSearchUri = detailSearchUri;
	}

}
