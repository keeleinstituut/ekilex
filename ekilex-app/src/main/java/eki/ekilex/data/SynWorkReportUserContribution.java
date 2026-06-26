package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class SynWorkReportUserContribution extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String userName;

	private int completedWordCount;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getCompletedWordCount() {
		return completedWordCount;
	}

	public void setCompletedWordCount(int completedWordCount) {
		this.completedWordCount = completedWordCount;
	}
}
