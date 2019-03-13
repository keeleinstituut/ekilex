package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class EkiUserPermData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private String email;

	private boolean enabled;

	private boolean admin;

	private Boolean approved;

	private boolean approvingPending;

	private List<EkiUserApplication> applications;

	private List<DatasetPermission> datasetPermissions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public boolean isApprovingPending() {
		return approvingPending;
	}

	public void setApprovingPending(boolean approvingPending) {
		this.approvingPending = approvingPending;
	}

	public List<EkiUserApplication> getApplications() {
		return applications;
	}

	public void setApplications(List<EkiUserApplication> applications) {
		this.applications = applications;
	}

	public List<DatasetPermission> getDatasetPermissions() {
		return datasetPermissions;
	}

	public void setDatasetPermissions(List<DatasetPermission> datasetPermissions) {
		this.datasetPermissions = datasetPermissions;
	}

}
