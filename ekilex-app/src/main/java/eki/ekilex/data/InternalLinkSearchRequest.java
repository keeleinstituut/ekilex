package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class InternalLinkSearchRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String searchFilter;

	public String getSearchFilter() {
		return searchFilter;
	}

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}
}
