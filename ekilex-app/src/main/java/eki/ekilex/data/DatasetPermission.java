package eki.ekilex.data;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.data.AbstractDataObject;

public class DatasetPermission extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long userId;

	private String datasetCode;

	private String datasetName;

	private boolean isSuperiorDataset;

	private AuthorityOperation authOperation;

	private AuthorityItem authItem;

	private String authLang;

	private String authLangValue;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public boolean isSuperiorDataset() {
		return isSuperiorDataset;
	}

	public void setSuperiorDataset(boolean superiorDataset) {
		isSuperiorDataset = superiorDataset;
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

	public String getAuthLangValue() {
		return authLangValue;
	}

	public void setAuthLangValue(String authLangValue) {
		this.authLangValue = authLangValue;
	}

}
