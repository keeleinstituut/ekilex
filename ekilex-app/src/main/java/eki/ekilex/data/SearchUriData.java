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

	private boolean fetchAll;

	public SearchUriData(boolean valid, String searchMode, List<String> selectedDatasets, String simpleSearchFilter, SearchFilter detailSearchFilter, boolean fetchAll) {
		this.valid = valid;
		this.searchMode = searchMode;
		this.selectedDatasets = selectedDatasets;
		this.simpleSearchFilter = simpleSearchFilter;
		this.detailSearchFilter = detailSearchFilter;
		this.fetchAll = fetchAll;
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

	public boolean isFetchAll() {
		return fetchAll;
	}
}
