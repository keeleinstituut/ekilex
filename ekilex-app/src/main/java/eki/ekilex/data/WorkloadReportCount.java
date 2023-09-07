package eki.ekilex.data;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.CrudType;

public class WorkloadReportCount extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private ActivityOwner activityOwner;

	private ActivityEntity activityEntity;

	private CrudType activityType;

	private String functName;

	private String userName;

	private int count;

	public ActivityOwner getActivityOwner() {
		return activityOwner;
	}

	public void setActivityOwner(ActivityOwner activityOwner) {
		this.activityOwner = activityOwner;
	}

	public ActivityEntity getActivityEntity() {
		return activityEntity;
	}

	public void setActivityEntity(ActivityEntity activityEntity) {
		this.activityEntity = activityEntity;
	}

	public CrudType getActivityType() {
		return activityType;
	}

	public void setActivityType(CrudType activityType) {
		this.activityType = activityType;
	}

	public String getFunctName() {
		return functName;
	}

	public void setFunctName(String functName) {
		this.functName = functName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
