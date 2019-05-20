package eki.ekilex.data;

import java.sql.Timestamp;

import eki.common.data.AbstractDataObject;

public class ProcessLog extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String eventBy;

	private Timestamp eventOn;

	private String comment;

	private String processStateCode;

	private String datasetCode;

	public String getEventBy() {
		return eventBy;
	}

	public void setEventBy(String eventBy) {
		this.eventBy = eventBy;
	}

	public Timestamp getEventOn() {
		return eventOn;
	}

	public void setEventOn(Timestamp eventOn) {
		this.eventOn = eventOn;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getProcessStateCode() {
		return processStateCode;
	}

	public void setProcessStateCode(String processStateCode) {
		this.processStateCode = processStateCode;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}
}
