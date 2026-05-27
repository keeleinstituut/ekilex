package eki.ekilex.data;

import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.ReportType;

public class ReportQueueContent extends AbstractDataObject implements QueueContent {

	private static final long serialVersionUID = 1L;

	private Long reportId;

	private ReportType type;

	private ReportParameters parameters;

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public ReportType getType() {
		return type;
	}

	public void setType(ReportType type) {
		this.type = type;
	}

	public ReportParameters getParameters() {
		return parameters;
	}

	public void setParameters(ReportParameters parameters) {
		this.parameters = parameters;
	}
}
