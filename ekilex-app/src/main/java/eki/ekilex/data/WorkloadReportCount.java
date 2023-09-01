package eki.ekilex.data;

import eki.common.constant.ActivityOwner;
import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.CrudType;

public class WorkloadReportCount extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String userName;

	private ActivityOwner activityOwner;

	private CrudType activityType;

	private int count;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ActivityOwner getActivityOwner() {
		return activityOwner;
	}

	public void setActivityOwner(ActivityOwner activityOwner) {
		this.activityOwner = activityOwner;
	}

	public CrudType getActivityType() {
		return activityType;
	}

	public void setActivityType(CrudType activityType) {
		this.activityType = activityType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
