package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WorkloadFunctionReport extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String functName;

	private List<WorkloadReportUser> functionReportUsers;

	public String getFunctName() {
		return functName;
	}

	public void setFunctName(String functName) {
		this.functName = functName;
	}

	public List<WorkloadReportUser> getFunctionReportUsers() {
		return functionReportUsers;
	}

	public void setFunctionReportUsers(List<WorkloadReportUser> functionReportUsers) {
		this.functionReportUsers = functionReportUsers;
	}
}
