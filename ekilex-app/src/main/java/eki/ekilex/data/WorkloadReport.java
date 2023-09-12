package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WorkloadReport extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> userNames;

	private List<WorkloadReportRow> reportRows;

	private int resultCount;

	public List<String> getUserNames() {
		return userNames;
	}

	public void setUserNames(List<String> userNames) {
		this.userNames = userNames;
	}

	public List<WorkloadReportRow> getReportRows() {
		return reportRows;
	}

	public void setReportRows(List<WorkloadReportRow> reportRows) {
		this.reportRows = reportRows;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
}
