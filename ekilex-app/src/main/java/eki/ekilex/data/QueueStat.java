package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class QueueStat extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String groupId;

	private long stepCount;

	public QueueStat() {
	}

	public QueueStat(String groupId, long stepCount) {
		this.groupId = groupId;
		this.stepCount = stepCount;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public long getStepCount() {
		return stepCount;
	}

	public void setStepCount(long stepCount) {
		this.stepCount = stepCount;
	}

}
