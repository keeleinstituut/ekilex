package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class EkiUserProfile extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long recentDatasetPermissionId;

	public Long getRecentDatasetPermissionId() {
		return recentDatasetPermissionId;
	}

	public void setRecentDatasetPermissionId(Long recentDatasetPermissionId) {
		this.recentDatasetPermissionId = recentDatasetPermissionId;
	}

}
