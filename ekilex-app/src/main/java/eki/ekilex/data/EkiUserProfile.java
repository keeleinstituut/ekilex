package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class EkiUserProfile extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long recentDatasetPermissionId;

	private List<String> preferredDatasets;

	public Long getRecentDatasetPermissionId() {
		return recentDatasetPermissionId;
	}

	public void setRecentDatasetPermissionId(Long recentDatasetPermissionId) {
		this.recentDatasetPermissionId = recentDatasetPermissionId;
	}

	public List<String> getPreferredDatasets() {
		return preferredDatasets;
	}

	public void setPreferredDatasets(List<String> preferredDatasets) {
		this.preferredDatasets = preferredDatasets;
	}
}
