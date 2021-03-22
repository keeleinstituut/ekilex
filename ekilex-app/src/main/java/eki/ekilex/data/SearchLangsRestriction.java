package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class SearchLangsRestriction extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> filteringLangs;

	private boolean noLangsFiltering;

	public List<String> getFilteringLangs() {
		return filteringLangs;
	}

	public void setFilteringLangs(List<String> filteringLangs) {
		this.filteringLangs = filteringLangs;
	}

	public boolean isNoLangsFiltering() {
		return noLangsFiltering;
	}

	public void setNoLangsFiltering(boolean noLangsFiltering) {
		this.noLangsFiltering = noLangsFiltering;
	}
}
