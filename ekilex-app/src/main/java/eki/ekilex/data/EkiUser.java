package eki.ekilex.data;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class EkiUser implements Principal, Serializable {

	private static final long serialVersionUID = 1L;

	private static final String[] HIDDEN_FIELD_NAMES = new String[] {"password", "activationKey", "recoveryKey", "apiKey"};

	private Long id;

	private String name;

	private String email;

	private String password;

	private String activationKey;

	private String recoveryKey;

	private String apiKey;

	private boolean apiCrud;

	private boolean admin;

	private boolean master;

	private Boolean enabled;

	private List<DatasetPermission> datasetPermissions;

	private boolean datasetPermissionsExist;

	private boolean datasetCrudPermissionsExist;

	private boolean datasetOwnershipExist;

	private boolean hasSingleDatasetPermission;

	private DatasetPermission recentRole;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public String getRecoveryKey() {
		return recoveryKey;
	}

	public void setRecoveryKey(String recoveryKey) {
		this.recoveryKey = recoveryKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
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

	public List<DatasetPermission> getDatasetPermissions() {
		return datasetPermissions;
	}

	public void setDatasetPermissions(List<DatasetPermission> datasetPermissions) {
		this.datasetPermissions = datasetPermissions;
	}

	public boolean isDatasetPermissionsExist() {
		return datasetPermissionsExist;
	}

	public void setDatasetPermissionsExist(boolean datasetPermissionsExist) {
		this.datasetPermissionsExist = datasetPermissionsExist;
	}

	public boolean isDatasetCrudPermissionsExist() {
		return datasetCrudPermissionsExist;
	}

	public void setDatasetCrudPermissionsExist(boolean datasetCrudPermissionsExist) {
		this.datasetCrudPermissionsExist = datasetCrudPermissionsExist;
	}

	public boolean isDatasetOwnershipExist() {
		return datasetOwnershipExist;
	}

	public void setDatasetOwnershipExist(boolean datasetOwnershipExist) {
		this.datasetOwnershipExist = datasetOwnershipExist;
	}

	public boolean isHasSingleDatasetPermission() {
		return hasSingleDatasetPermission;
	}

	public void setHasSingleDatasetPermission(boolean hasSingleDatasetPermission) {
		this.hasSingleDatasetPermission = hasSingleDatasetPermission;
	}

	public DatasetPermission getRecentRole() {
		return recentRole;
	}

	public void setRecentRole(DatasetPermission recentRole) {
		this.recentRole = recentRole;
	}

	@Override
	public boolean equals(Object object) {
		return EqualsBuilder.reflectionEquals(this, object, HIDDEN_FIELD_NAMES);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, HIDDEN_FIELD_NAMES);
	}

	@Override
	public String toString() {
		ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		reflectionToStringBuilder.setExcludeFieldNames(HIDDEN_FIELD_NAMES);
		return reflectionToStringBuilder.toString();
	}

	public String getDescription() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append('[');
		sbuf.append(id);
		sbuf.append(", ");
		sbuf.append(name);
		sbuf.append(", ");
		sbuf.append(email);
		sbuf.append(']');
		return sbuf.toString();
	}
}
