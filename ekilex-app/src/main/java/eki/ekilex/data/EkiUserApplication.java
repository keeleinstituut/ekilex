package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class EkiUserApplication extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long userId;

	private List<String> datasetCodes;

	private List<Dataset> datasets;

	private String comment;

	private Boolean approved;

	private Timestamp created;

	private boolean basicApplication;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

	public List<Dataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<Dataset> datasets) {
		this.datasets = datasets;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public boolean isBasicApplication() {
		return basicApplication;
	}

	public void setBasicApplication(boolean basicApplication) {
		this.basicApplication = basicApplication;
	}

}
