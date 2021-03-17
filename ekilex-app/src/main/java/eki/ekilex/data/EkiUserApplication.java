package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class EkiUserApplication extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long userId;

	private List<String> datasetCodes;

	private List<Dataset> datasets;

	private String comment;

	private boolean reviewed;

	private Timestamp created;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public boolean isReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

}
