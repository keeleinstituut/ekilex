package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermDatasetReportContent extends AbstractDataObject implements ReportContent {

	private static final long serialVersionUID = 1L;

	private TermDatasetReportParameters parameters;

	private List<TermDatasetReportRow> rows;

	public TermDatasetReportParameters getParameters() {
		return parameters;
	}

	public void setParameters(TermDatasetReportParameters parameters) {
		this.parameters = parameters;
	}

	public List<TermDatasetReportRow> getRows() {
		return rows;
	}

	public void setRows(List<TermDatasetReportRow> rows) {
		this.rows = rows;
	}
}
