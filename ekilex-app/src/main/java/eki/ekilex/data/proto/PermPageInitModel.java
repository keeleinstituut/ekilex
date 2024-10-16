package eki.ekilex.data.proto;

import java.util.List;

import eki.common.constant.AuthorityOperation;
import eki.common.data.AbstractDataObject;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUserRoleData;

public class PermPageInitModel extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private EkiUserRoleData userRoleData;

	private List<Dataset> userOwnedDatasets;

	private List<AuthorityOperation> authorityOperations;

	private List<Classifier> languages;

	public EkiUserRoleData getUserRoleData() {
		return userRoleData;
	}

	public void setUserRoleData(EkiUserRoleData userRoleData) {
		this.userRoleData = userRoleData;
	}

	public List<Dataset> getUserOwnedDatasets() {
		return userOwnedDatasets;
	}

	public void setUserOwnedDatasets(List<Dataset> userOwnedDatasets) {
		this.userOwnedDatasets = userOwnedDatasets;
	}

	public List<AuthorityOperation> getAuthorityOperations() {
		return authorityOperations;
	}

	public void setAuthorityOperations(List<AuthorityOperation> authorityOperations) {
		this.authorityOperations = authorityOperations;
	}

	public List<Classifier> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Classifier> languages) {
		this.languages = languages;
	}

}
