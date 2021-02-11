package eki.ekilex.web.bean;

import eki.common.constant.OrderingField;
import eki.common.data.AbstractDataObject;

public class PermSearchBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String userNameFilter;

	private String userPermDatasetCodeFilter;

	private Boolean userEnablePendingFilter;

	private OrderingField orderBy;

	public String getUserNameFilter() {
		return userNameFilter;
	}

	public void setUserNameFilter(String userNameFilter) {
		this.userNameFilter = userNameFilter;
	}

	public String getUserPermDatasetCodeFilter() {
		return userPermDatasetCodeFilter;
	}

	public void setUserPermDatasetCodeFilter(String userPermDatasetCodeFilter) {
		this.userPermDatasetCodeFilter = userPermDatasetCodeFilter;
	}

	public Boolean getUserEnablePendingFilter() {
		return userEnablePendingFilter;
	}

	public void setUserEnablePendingFilter(Boolean userEnablePendingFilter) {
		this.userEnablePendingFilter = userEnablePendingFilter;
	}

	public OrderingField getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrderingField orderBy) {
		this.orderBy = orderBy;
	}

}
