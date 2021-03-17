package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class FeedbackLogResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<FeedbackLog> feedbackLogs;

	private int pageNum;

	private int pageCount;

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public List<FeedbackLog> getFeedbackLogs() {
		return feedbackLogs;
	}

	public void setFeedbackLogs(List<FeedbackLog> feedbackLogs) {
		this.feedbackLogs = feedbackLogs;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

}
