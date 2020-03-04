package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class SearchDatasetsRestriction extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> filteringDatasetCodes;

	private List<String> userPermDatasetCodes;

	private List<String> availableDatasetCodes;

	private boolean noDatasetsFiltering;

	private boolean allDatasetsPermissions;

	private boolean singleFilteringDataset;

	private boolean singlePermDataset;

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

	public List<String> getAvailableDatasetCodes() {
		return availableDatasetCodes;
	}

	public void setAvailableDatasetCodes(List<String> availableDatasetCodes) {
		this.availableDatasetCodes = availableDatasetCodes;
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

	public boolean isSingleFilteringDataset() {
		return singleFilteringDataset;
	}

	public void setSingleFilteringDataset(boolean singleFilteringDataset) {
		this.singleFilteringDataset = singleFilteringDataset;
	}

	public boolean isSinglePermDataset() {
		return singlePermDataset;
	}

	public void setSinglePermDataset(boolean singlePermDataset) {
		this.singlePermDataset = singlePermDataset;
	}

}
