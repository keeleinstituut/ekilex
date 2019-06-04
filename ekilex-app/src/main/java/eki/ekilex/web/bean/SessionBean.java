package eki.ekilex.web.bean;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;

public class SessionBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> selectedDatasets;

	private String resultLang;

	private List<ClassifierSelect> languagesOrder;

	private String newWordSelectedDataset;

	private String newWordSelectedLanguage;

	private String newWordSelectedMorphCode;

	private DatasetPermission selectedDatasetPermission;

	private boolean adminRoleSelected;

	public List<String> getSelectedDatasets() {
		return selectedDatasets;
	}

	public void setSelectedDatasets(List<String> selectedDatasets) {
		this.selectedDatasets = selectedDatasets;
	}

	public String getResultLang() {
		return resultLang;
	}

	public void setResultLang(String resultLang) {
		this.resultLang = resultLang;
	}

	public List<ClassifierSelect> getLanguagesOrder() {
		return languagesOrder;
	}

	public void setLanguagesOrder(List<ClassifierSelect> languagesOrder) {
		this.languagesOrder = languagesOrder;
	}

	public String getNewWordSelectedDataset() {
		return newWordSelectedDataset;
	}

	public void setNewWordSelectedDataset(String newWordSelectedDataset) {
		this.newWordSelectedDataset = newWordSelectedDataset;
	}

	public String getNewWordSelectedLanguage() {
		return newWordSelectedLanguage;
	}

	public void setNewWordSelectedLanguage(String newWordSelectedLanguage) {
		this.newWordSelectedLanguage = newWordSelectedLanguage;
	}

	public String getNewWordSelectedMorphCode() {
		return newWordSelectedMorphCode;
	}

	public void setNewWordSelectedMorphCode(String newWordSelectedMorphCode) {
		this.newWordSelectedMorphCode = newWordSelectedMorphCode;
	}

	public DatasetPermission getSelectedDatasetPermission() {
		return selectedDatasetPermission;
	}

	public void setSelectedDatasetPermission(DatasetPermission selectedDatasetPermission) {
		this.selectedDatasetPermission = selectedDatasetPermission;
	}

	public boolean isAdminRoleSelected() {
		return adminRoleSelected;
	}

	public void setAdminRoleSelected(boolean adminRoleSelected) {
		this.adminRoleSelected = adminRoleSelected;
	}
}
