package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class SearchUriData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean valid;

	private String searchMode;

	private List<String> selectedDatasets;

	private String simpleSearchFilter;

	private SearchFilter detailSearchFilter;

	public SearchUriData(boolean valid, String searchMode, List<String> selectedDatasets, String simpleSearchFilter, SearchFilter detailSearchFilter) {
		this.valid = valid;
		this.searchMode = searchMode;
		this.selectedDatasets = selectedDatasets;
		this.simpleSearchFilter = simpleSearchFilter;
		this.detailSearchFilter = detailSearchFilter;
	}

	public boolean isValid() {
		return valid;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public List<String> getSelectedDatasets() {
		return selectedDatasets;
	}

	public String getSimpleSearchFilter() {
		return simpleSearchFilter;
	}

	public SearchFilter getDetailSearchFilter() {
		return detailSearchFilter;
	}

}
