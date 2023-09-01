package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.data.AbstractDataObject;

public class ActivityLog extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String eventBy;

	private Timestamp eventOn;

	private String datasetCode;

	private String functName;

	private Long ownerId;

	private ActivityOwner ownerName;

	private Long entityId;

	private ActivityEntity entityName;

	private String prevData;

	private String currData;

	private List<TypeActivityLogDiff> prevDiffs;

	private List<TypeActivityLogDiff> currDiffs;

	private String wordValue;

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

	public Timestamp getEventOn() {
		return eventOn;
	}

	public void setEventOn(Timestamp eventOn) {
		this.eventOn = eventOn;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getFunctName() {
		return functName;
	}

	public void setFunctName(String functName) {
		this.functName = functName;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public ActivityOwner getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(ActivityOwner ownerName) {
		this.ownerName = ownerName;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public ActivityEntity getEntityName() {
		return entityName;
	}

	public void setEntityName(ActivityEntity entityName) {
		this.entityName = entityName;
	}

	public String getPrevData() {
		return prevData;
	}

	public void setPrevData(String prevData) {
		this.prevData = prevData;
	}

	public String getCurrData() {
		return currData;
	}

	public void setCurrData(String currData) {
		this.currData = currData;
	}

	public List<TypeActivityLogDiff> getPrevDiffs() {
		return prevDiffs;
	}

	public void setPrevDiffs(List<TypeActivityLogDiff> prevDiffs) {
		this.prevDiffs = prevDiffs;
	}

	public List<TypeActivityLogDiff> getCurrDiffs() {
		return currDiffs;
	}

	public void setCurrDiffs(List<TypeActivityLogDiff> currDiffs) {
		this.currDiffs = currDiffs;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

}
