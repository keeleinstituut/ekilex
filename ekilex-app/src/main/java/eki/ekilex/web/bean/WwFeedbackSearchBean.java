package eki.ekilex.web.bean;

import eki.common.data.AbstractDataObject;

public class WwFeedbackSearchBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String senderEmailFilter;

	private Boolean notCommentedFilter;

	private int pageNum;

	public String getSenderEmailFilter() {
		return senderEmailFilter;
	}

	public void setSenderEmailFilter(String senderEmailFilter) {
		this.senderEmailFilter = senderEmailFilter;
	}

	public Boolean getNotCommentedFilter() {
		return notCommentedFilter;
	}

	public void setNotCommentedFilter(Boolean notCommentedFilter) {
		this.notCommentedFilter = notCommentedFilter;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

}
