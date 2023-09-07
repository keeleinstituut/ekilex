package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WorkloadReportUserCount extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String userName;

	private int count;

	private List<Long> ownerIds;

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
}