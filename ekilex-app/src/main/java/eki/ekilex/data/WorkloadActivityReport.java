package eki.ekilex.data;

import java.util.List;

import eki.common.constant.ActivityOwner;
import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.CrudType;

public class WorkloadActivityReport extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private ActivityOwner activityOwner;

	private CrudType activityType;

	private List<WorkloadReportUser> activityReportUsers;

	private int totalCount;

	private List<WorkloadFunctionReport> functionReports;

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

	public List<WorkloadReportUser> getActivityReportUsers() {
		return activityReportUsers;
	}

	public void setActivityReportUsers(List<WorkloadReportUser> activityReportUsers) {
		this.activityReportUsers = activityReportUsers;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<WorkloadFunctionReport> getFunctionReports() {
		return functionReports;
	}

	public void setFunctionReports(List<WorkloadFunctionReport> functionReports) {
		this.functionReports = functionReports;
	}
}
