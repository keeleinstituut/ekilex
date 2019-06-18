package eki.ekilex.data.transform;

import java.sql.Timestamp;

import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.common.data.AbstractDataObject;

public class LexemeLifecycleLog extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long entityId;

	private LifecycleEntity entity;

	private LifecycleProperty property;

	private LifecycleEventType eventType;

	private String eventBy;

	private Timestamp eventOn;

	private String recent;

	private String entry;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public LifecycleEntity getEntity() {
		return entity;
	}

	public void setEntity(LifecycleEntity entity) {
		this.entity = entity;
	}

	public LifecycleProperty getProperty() {
		return property;
	}

	public void setProperty(LifecycleProperty property) {
		this.property = property;
	}

	public LifecycleEventType getEventType() {
		return eventType;
	}

	public void setEventType(LifecycleEventType eventType) {
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
