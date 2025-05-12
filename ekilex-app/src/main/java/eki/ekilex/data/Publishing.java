package eki.ekilex.data;

import java.time.LocalDateTime;

import eki.common.data.AbstractDataObject;

public class Publishing extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String eventBy;

	private LocalDateTime eventOn;

	private String targetName;

	private String entityName;

	private Long entityId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEventBy() {
		return eventBy;
	}

	public void setEventBy(String eventBy) {
		this.eventBy = eventBy;
	}

	public LocalDateTime getEventOn() {
		return eventOn;
	}

	public void setEventOn(LocalDateTime eventOn) {
		this.eventOn = eventOn;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

}
