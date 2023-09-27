package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WorkloadReport extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> userNames;

	private List<WorkloadActivityReport> activityReports;

	private int resultCount;

	public List<String> getUserNames() {
		return userNames;
	}

	public void setUserNames(List<String> userNames) {
		this.userNames = userNames;
	}

	public List<WorkloadActivityReport> getActivityReports() {
		return activityReports;
	}

	public void setActivityReports(List<WorkloadActivityReport> activityReports) {
		this.activityReports = activityReports;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
}
