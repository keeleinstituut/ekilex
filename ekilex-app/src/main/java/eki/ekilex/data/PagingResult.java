package eki.ekilex.data;

import eki.common.data.AbstractDataObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"showPaging", "previousPageExists", "nextPageExists", "currentPage", "totalPages", "offset"})
public class PagingResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean showPaging;

	private boolean previousPageExists;

	private boolean nextPageExists;

	private int currentPage;

	private int totalPages;

	private int offset;

	public boolean isShowPaging() {
		return showPaging;
	}

	public void setShowPaging(boolean showPaging) {
		this.showPaging = showPaging;
	}

	public boolean isPreviousPageExists() {
		return previousPageExists;
	}

	public void setPreviousPageExists(boolean previousPageExists) {
		this.previousPageExists = previousPageExists;
	}

	public boolean isNextPageExists() {
		return nextPageExists;
	}

	public void setNextPageExists(boolean nextPageExists) {
		this.nextPageExists = nextPageExists;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}