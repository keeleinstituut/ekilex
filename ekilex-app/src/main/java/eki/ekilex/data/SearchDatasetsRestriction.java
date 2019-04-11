package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class SearchDatasetsRestriction extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> filteringDatasetCodes;

	private List<String> userPermDatasetCodes;

	private boolean noDatasetsFiltering;

	private boolean allDatasetsPermissions;

	public List<String> getFilteringDatasetCodes() {
		return filteringDatasetCodes;
	}

	public void setFilteringDatasetCodes(List<String> filteringDatasetCodes) {
		this.filteringDatasetCodes = filteringDatasetCodes;
	}

	public List<String> getUserPermDatasetCodes() {
		return userPermDatasetCodes;
	}

	public void setUserPermDatasetCodes(List<String> userPermDatasetCodes) {
		this.userPermDatasetCodes = userPermDatasetCodes;
	}

	public boolean isNoDatasetsFiltering() {
		return noDatasetsFiltering;
	}

	public void setNoDatasetsFiltering(boolean noDatasetsFiltering) {
		this.noDatasetsFiltering = noDatasetsFiltering;
	}

	public boolean isAllDatasetsPermissions() {
		return allDatasetsPermissions;
	}

	public void setAllDatasetsPermissions(boolean allDatasetsPermissions) {
		this.allDatasetsPermissions = allDatasetsPermissions;
	}

}
