package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class EkiUserPermData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private String email;

	private boolean admin;

	private Boolean enabled;

	private Boolean reviewed;

	private String reviewComment;

	private Timestamp createdOn;

	private boolean enablePending;

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

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getReviewed() {
		return reviewed;
	}

	public void setReviewed(Boolean reviewed) {
		this.reviewed = reviewed;
	}

	public String getReviewComment() {
		return reviewComment;
	}

	public void setReviewComment(String reviewComment) {
		this.reviewComment = reviewComment;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public boolean isEnablePending() {
		return enablePending;
	}

	public void setEnablePending(boolean enablePending) {
		this.enablePending = enablePending;
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
