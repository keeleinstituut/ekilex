package eki.ekilex.data;

import java.io.Serializable;

public class UserRole implements Serializable {

	private static final long serialVersionUID = 4103399713081501194L;

	private DatasetPermission datasetPermission;

	private boolean admin;

	public DatasetPermission getDatasetPermission() {
		return datasetPermission;
	}

	public void setDatasetPermission(DatasetPermission datasetPermission) {
		this.datasetPermission = datasetPermission;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
