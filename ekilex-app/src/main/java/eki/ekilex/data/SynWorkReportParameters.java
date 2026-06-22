package eki.ekilex.data;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import eki.common.data.AbstractDataObject;

public class SynWorkReportParameters extends AbstractDataObject implements ReportParameters {

	private static final long serialVersionUID = 1L;

	@DateTimeFormat(pattern = "dd.MM.yyyy")
	private LocalDate dateFrom;

	@DateTimeFormat(pattern = "dd.MM.yyyy")
	private LocalDate dateUntil;

	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}

	public LocalDate getDateUntil() {
		return dateUntil;
	}

	public void setDateUntil(LocalDate dateUntil) {
		this.dateUntil = dateUntil;
	}
}
