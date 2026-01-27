package eki.ekilex.data;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import eki.common.constant.FeedbackType;
import eki.common.data.AbstractDataObject;

public class FeedbackSearchFilter extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private FeedbackType feedbackType;

	private String searchValue;

	@DateTimeFormat(pattern = "dd.MM.yyyy")
	private LocalDate created;

	private Boolean notCommented;

	private int pageNum;

	public FeedbackType getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(FeedbackType feedbackType) {
		this.feedbackType = feedbackType;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public LocalDate getCreated() {
		return created;
	}

	public void setCreated(LocalDate created) {
		this.created = created;
	}

	public Boolean getNotCommented() {
		return notCommented;
	}

	public void setNotCommented(Boolean notCommented) {
		this.notCommented = notCommented;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

}
