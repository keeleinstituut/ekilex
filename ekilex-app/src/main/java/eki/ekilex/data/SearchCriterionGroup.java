package eki.ekilex.data;

import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.SearchEntity;

import java.util.List;

public class SearchCriterionGroup extends AbstractDataObject {

	private SearchEntity entity;

	private List<SearchCriterion> searchCriteria;

	public SearchEntity getEntity() {
		return entity;
	}

	public void setEntity(SearchEntity entity) {
		this.entity = entity;
	}

	public List<SearchCriterion> getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(List<SearchCriterion> searchCriteria) {
		this.searchCriteria = searchCriteria;
	}
}
