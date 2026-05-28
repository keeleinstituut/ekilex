package eki.ekilex.data;

import java.time.LocalDateTime;

import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.ReportStatus;
import eki.ekilex.constant.ReportType;

public class Report extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private ReportType type;

	private ReportStatus status;

	private LocalDateTime createdOn;

	private LocalDateTime completedOn;

	private String content;

	private String userName;

	private boolean pending;

	private boolean completed;

	private boolean deletable;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ReportType getType() {
		return type;
	}

	public void settType(ReportType type) {
		this.type = type;
	}

	public ReportStatus getStatus() {
		return status;
	}

	public void setStatus(ReportStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(LocalDateTime completedOn) {
		this.completedOn = completedOn;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}
}
