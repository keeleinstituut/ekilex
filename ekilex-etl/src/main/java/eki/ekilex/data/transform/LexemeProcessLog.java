package eki.ekilex.data.transform;

import java.sql.Timestamp;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class LexemeProcessLog extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private String eventBy;

	private Timestamp eventOn;

	private String comment;

	private String processStateCode;

	private String datasetCode;

	private boolean sourceLinksExist;

	private List<ProcessLogSourceLink> sourceLinks;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

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

	public boolean isSourceLinksExist() {
		return sourceLinksExist;
	}

	public void setSourceLinksExist(boolean sourceLinksExist) {
		this.sourceLinksExist = sourceLinksExist;
	}

	public List<ProcessLogSourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<ProcessLogSourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
