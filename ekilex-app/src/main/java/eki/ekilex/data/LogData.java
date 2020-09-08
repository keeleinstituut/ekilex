package eki.ekilex.data;

import org.apache.commons.lang3.StringUtils;

import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.common.data.AbstractDataObject;

@Deprecated
public class LogData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private LifecycleEventType eventType;

	private LifecycleEntity entityName;

	private Long entityId;

	private LifecycleProperty property;

	private String recent;

	private String entry;

	private String userName;

	private ListData listData;

	public LogData() {
	}

	public LogData(LifecycleEventType eventType, LifecycleEntity entityName, LifecycleProperty property, Long entityId) {
		this.eventType = eventType;
		this.entityName = entityName;
		this.property = property;
		this.entityId = entityId;
	}

	public LogData(LifecycleEventType eventType, LifecycleEntity entityName, LifecycleProperty property, Long entityId, String entry) {
		this.eventType = eventType;
		this.entityName = entityName;
		this.property = property;
		this.entityId = entityId;
		this.entry = entry;
	}

	public LogData(LifecycleEventType eventType, LifecycleEntity entityName, LifecycleProperty property, Long entityId, String recent, String entry) {
		this.eventType = eventType;
		this.entityName = entityName;
		this.property = property;
		this.entityId = entityId;
		this.recent = recent;
		this.entry = entry;
	}

	public LogData(LifecycleEventType eventType, LifecycleEntity entityName, LifecycleProperty property, ListData listData) {
		this.eventType = eventType;
		this.entityName = entityName;
		this.property = property;
		this.listData = listData;
	}

	public LifecycleEventType getEventType() {
		return eventType;
	}

	public void setEventType(LifecycleEventType eventType) {
		this.eventType = eventType;
	}

	public LifecycleEntity getEntityName() {
		return entityName;
	}

	public void setEntityName(LifecycleEntity entityName) {
		this.entityName = entityName;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public LifecycleProperty getProperty() {
		return property;
	}

	public void setProperty(LifecycleProperty property) {
		this.property = property;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ListData getListData() {
		return listData;
	}

	public void setListData(ListData listData) {
		this.listData = listData;
	}

	public boolean isValueChanged() {
		return !StringUtils.equals(recent, entry);
	}

	public boolean isUpdateEvent() {
		return LifecycleEventType.UPDATE.equals(eventType);
	}
}
