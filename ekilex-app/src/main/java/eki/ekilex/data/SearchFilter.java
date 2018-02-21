package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class SearchFilter extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<SearchCriterion> searchCriteria;

	private List<SearchCriterionGroup> criteriaGroups;

	public List<SearchCriterion> getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(List<SearchCriterion> searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public List<SearchCriterionGroup> getCriteriaGroups() {
		return criteriaGroups;
	}

	public void setCriteriaGroups(List<SearchCriterionGroup> criteriaGroups) {
		this.criteriaGroups = criteriaGroups;
	}
}
