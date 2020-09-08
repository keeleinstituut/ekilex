package eki.ekilex.data;

import java.sql.Timestamp;

import eki.common.data.AbstractDataObject;

@Deprecated
public class LifecycleLog extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long entityId;

	private String entityName;

	private String entityProp;

	private String eventType;

	private String eventBy;

	private Timestamp eventOn;

	private String recent;

	private String entry;

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityProp() {
		return entityProp;
	}

	public void setEntityProp(String entityProp) {
		this.entityProp = entityProp;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
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

	public String getRecent() {
		return recent;
	}

	public void setRecent(String recent) {
		this.recent = recent;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

}
