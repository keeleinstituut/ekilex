package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class EkiUserRoleData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean admin;

	private boolean roleSelected;

	private boolean crudRoleSelected;

	private boolean datasetOwnerOrAdmin;

	private boolean datasetCrudOwnerOrAdmin;

	private boolean roleChangeEnabled;

	private boolean layerChangeEnabled;

	private boolean processStateChangeEnabled;

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isRoleSelected() {
		return roleSelected;
	}

	public void setRoleSelected(boolean roleSelected) {
		this.roleSelected = roleSelected;
	}

	public boolean isCrudRoleSelected() {
		return crudRoleSelected;
	}

	public void setCrudRoleSelected(boolean crudRoleSelected) {
		this.crudRoleSelected = crudRoleSelected;
	}

	public boolean isDatasetOwnerOrAdmin() {
		return datasetOwnerOrAdmin;
	}

	public void setDatasetOwnerOrAdmin(boolean datasetOwnerOrAdmin) {
		this.datasetOwnerOrAdmin = datasetOwnerOrAdmin;
	}

	public boolean isDatasetCrudOwnerOrAdmin() {
		return datasetCrudOwnerOrAdmin;
	}

	public void setDatasetCrudOwnerOrAdmin(boolean datasetCrudOwnerOrAdmin) {
		this.datasetCrudOwnerOrAdmin = datasetCrudOwnerOrAdmin;
	}

	public boolean isRoleChangeEnabled() {
		return roleChangeEnabled;
	}

	public void setRoleChangeEnabled(boolean roleChangeEnabled) {
		this.roleChangeEnabled = roleChangeEnabled;
	}

	public boolean isLayerChangeEnabled() {
		return layerChangeEnabled;
	}

	public void setLayerChangeEnabled(boolean layerChangeEnabled) {
		this.layerChangeEnabled = layerChangeEnabled;
	}

	public boolean isProcessStateChangeEnabled() {
		return processStateChangeEnabled;
	}

	public void setProcessStateChangeEnabled(boolean processStateChangeEnabled) {
		this.processStateChangeEnabled = processStateChangeEnabled;
	}
}
