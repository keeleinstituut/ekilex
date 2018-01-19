package eki.ekilex.data;

import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;

public class SearchCriterion extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private SearchKey searchKey;

	private SearchOperand searchOperand;

	private Object searchValue;

	public SearchKey getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(SearchKey searchKey) {
		this.searchKey = searchKey;
	}

	public SearchOperand getSearchOperand() {
		return searchOperand;
	}

	public void setSearchOperand(SearchOperand searchOperand) {
		this.searchOperand = searchOperand;
	}

	public Object getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(Object searchValue) {
		this.searchValue = searchValue;
	}

}
