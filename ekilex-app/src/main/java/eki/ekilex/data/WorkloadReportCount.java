package eki.ekilex.data;

import java.util.List;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.CrudType;

public class WorkloadReportCount extends AbstractDataObject { // TODO rename?

	private static final long serialVersionUID = 1L;

	private ActivityOwner activityOwner;

	private ActivityEntity activityEntity;

	private CrudType activityType;

	private String functName;

	private String userName;

	private int count;

	private List<Long> ownerIds;

	private List<Long> wordIds;

	private List<Long> meaningIds;

	private List<String> wordValues;

	public ActivityOwner getActivityOwner() {
		return activityOwner;
	}

	public void setActivityOwner(ActivityOwner activityOwner) {
		this.activityOwner = activityOwner;
	}

	public ActivityEntity getActivityEntity() {
		return activityEntity;
	}

	public void setActivityEntity(ActivityEntity activityEntity) {
		this.activityEntity = activityEntity;
	}

	public CrudType getActivityType() {
		return activityType;
	}

	public void setActivityType(CrudType activityType) {
		this.activityType = activityType;
	}

	public String getFunctName() {
		return functName;
	}

	public void setFunctName(String functName) {
		this.functName = functName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<Long> getOwnerIds() {
		return ownerIds;
	}

	public void setOwnerIds(List<Long> ownerIds) {
		this.ownerIds = ownerIds;
	}

	public List<Long> getWordIds() {
		return wordIds;
	}

	public void setWordIds(List<Long> wordIds) {
		this.wordIds = wordIds;
	}

	public List<Long> getMeaningIds() {
		return meaningIds;
	}

	public void setMeaningIds(List<Long> meaningIds) {
		this.meaningIds = meaningIds;
	}

	public List<String> getWordValues() {
		return wordValues;
	}

	public void setWordValues(List<String> wordValues) {
		this.wordValues = wordValues;
	}
}
