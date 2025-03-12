package eki.common.data;

import java.util.List;

public class StatSearchResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<ValueCount> valueCounts;

	private int totalResultCount;

	private int pageNum;

	private int pageCount;

	private boolean resultExists;

	private boolean prevPageExists;

	private boolean nextPageExists;

	public List<ValueCount> getValueCounts() {
		return valueCounts;
	}

	public void setValueCounts(List<ValueCount> valueCounts) {
		this.valueCounts = valueCounts;
	}

	public int getTotalResultCount() {
		return totalResultCount;
	}

	public void setTotalResultCount(int totalResultCount) {
		this.totalResultCount = totalResultCount;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public boolean isResultExists() {
		return resultExists;
	}

	public void setResultExists(boolean resultExists) {
		this.resultExists = resultExists;
	}

	public boolean isPrevPageExists() {
		return prevPageExists;
	}

	public void setPrevPageExists(boolean prevPageExists) {
		this.prevPageExists = prevPageExists;
	}

	public boolean isNextPageExists() {
		return nextPageExists;
	}

	public void setNextPageExists(boolean nextPageExists) {
		this.nextPageExists = nextPageExists;
	}

}
