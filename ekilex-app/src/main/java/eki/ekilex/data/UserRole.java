package eki.ekilex.data;

import java.io.Serializable;

/**
 * Role of the authenticated user.
 */

public class UserRole implements Serializable {

	private static final long serialVersionUID = 4103399713081501194L;

	private DatasetPermission selectedDatasetPermission;

	private boolean admin;

	public DatasetPermission getSelectedDatasetPermission() {
		return selectedDatasetPermission;
	}

	public void setSelectedDatasetPermission(DatasetPermission selectedDatasetPermission) {
		this.selectedDatasetPermission = selectedDatasetPermission;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
