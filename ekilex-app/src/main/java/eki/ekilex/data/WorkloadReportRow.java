package eki.ekilex.data;

import java.util.List;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.CrudType;

public class WorkloadReportRow extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private ActivityOwner activityOwner;

	private ActivityEntity activityEntity;

	private CrudType activityType;

	private String functName;

	private List<WorkloadReportUserCount> userCounts;

	private int totalCount;

	private List<WorkloadReportRow> functionRows;

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

	public List<WorkloadReportUserCount> getUserCounts() {
		return userCounts;
	}

	public void setUserCounts(List<WorkloadReportUserCount> userCounts) {
		this.userCounts = userCounts;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<WorkloadReportRow> getFunctionRows() {
		return functionRows;
	}

	public void setFunctionRows(List<WorkloadReportRow> functionRows) {
		this.functionRows = functionRows;
	}
}
