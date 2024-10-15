package eki.ekilex.data.proto;

import java.util.List;

import eki.common.constant.OrderingField;
import eki.common.data.AbstractDataObject;
import eki.ekilex.data.EkiUserPermData;
import eki.ekilex.data.EkiUserRoleData;

public class PermPageModel extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private EkiUserRoleData userRoleData;

	private String userNameFilter;

	private String userPermDatasetCodeFilter;

	private Boolean userEnablePendingFilter;

	private OrderingField orderBy;

	private List<EkiUserPermData> ekiUserPermissions;

	public EkiUserRoleData getUserRoleData() {
		return userRoleData;
	}

	public void setUserRoleData(EkiUserRoleData userRoleData) {
		this.userRoleData = userRoleData;
	}

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

	public List<EkiUserPermData> getEkiUserPermissions() {
		return ekiUserPermissions;
	}

	public void setEkiUserPermissions(List<EkiUserPermData> ekiUserPermissions) {
		this.ekiUserPermissions = ekiUserPermissions;
	}

}
