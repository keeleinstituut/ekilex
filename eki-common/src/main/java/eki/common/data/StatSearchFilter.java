package eki.common.data;

import eki.common.constant.StatType;

public class StatSearchFilter extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private StatType statType;

	private String searchMode;

	private String datasetCode;

	private String searchLang;

	private String dateFrom;

	private String dateUntil;

	private boolean trustworthyOnly;

	public StatType getStatType() {
		return statType;
	}

	public void setStatType(StatType statType) {
		this.statType = statType;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getSearchLang() {
		return searchLang;
	}

	public void setSearchLang(String searchLang) {
		this.searchLang = searchLang;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateUntil() {
		return dateUntil;
	}

	public void setDateUntil(String dateUntil) {
		this.dateUntil = dateUntil;
	}

	public boolean isTrustworthyOnly() {
		return trustworthyOnly;
	}

	public void setTrustworthyOnly(boolean trustworthyOnly) {
		this.trustworthyOnly = trustworthyOnly;
	}

}
