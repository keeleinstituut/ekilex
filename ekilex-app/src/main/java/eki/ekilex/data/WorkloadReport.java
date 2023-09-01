package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WorkloadReport extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<WorkloadReportRow> userReports;

	private WorkloadReportRow totalReport;

	public List<WorkloadReportRow> getUserReports() {
		return userReports;
	}

	public void setUserReports(List<WorkloadReportRow> userReports) {
		this.userReports = userReports;
	}

	public WorkloadReportRow getTotalReport() {
		return totalReport;
	}

	public void setTotalReport(WorkloadReportRow totalReport) {
		this.totalReport = totalReport;
	}
}
