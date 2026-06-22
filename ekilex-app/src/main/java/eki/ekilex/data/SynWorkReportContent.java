package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

import java.util.List;

public class SynWorkReportContent extends AbstractDataObject implements ReportContent {

	private static final long serialVersionUID = 1L;

	private SynWorkReportParameters parameters;

	private List<SynWorkReportUserContribution> userContributions;

	public SynWorkReportParameters getParameters() {
		return parameters;
	}

	public void setParameters(SynWorkReportParameters parameters) {
		this.parameters = parameters;
	}

	public List<SynWorkReportUserContribution> getUserContributions() {
		return userContributions;
	}

	public void setUserContributions(List<SynWorkReportUserContribution> userContributions) {
		this.userContributions = userContributions;
	}
}
