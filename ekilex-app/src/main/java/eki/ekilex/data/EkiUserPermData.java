package eki.ekilex.data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.data.AbstractDataObject;
import eki.common.util.LocalDateTimeDeserialiser;

public class EkiUserPermData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private String email;

	private boolean apiKeyExists;

	private boolean apiCrud;

	private boolean admin;

	private boolean master;

	private Boolean enabled;

	private String reviewComment;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime createdOn;

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

	public boolean isApiKeyExists() {
		return apiKeyExists;
	}

	public void setApiKeyExists(boolean apiKeyExists) {
		this.apiKeyExists = apiKeyExists;
	}

	public boolean isApiCrud() {
		return apiCrud;
	}

	public void setApiCrud(boolean apiCrud) {
		this.apiCrud = apiCrud;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getReviewComment() {
		return reviewComment;
	}

	public void setReviewComment(String reviewComment) {
		this.reviewComment = reviewComment;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
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
