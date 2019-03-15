package eki.ekilex.data;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.data.AbstractDataObject;

public class DatasetPermission extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String datasetCode;

	private AuthorityOperation authOperation;

	private AuthorityItem authItem;

	private String authLang;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public AuthorityOperation getAuthOperation() {
		return authOperation;
	}

	public void setAuthOperation(AuthorityOperation authOperation) {
		this.authOperation = authOperation;
	}

	public AuthorityItem getAuthItem() {
		return authItem;
	}

	public void setAuthItem(AuthorityItem authItem) {
		this.authItem = authItem;
	}

	public String getAuthLang() {
		return authLang;
	}

	public void setAuthLang(String authLang) {
		this.authLang = authLang;
	}

}
