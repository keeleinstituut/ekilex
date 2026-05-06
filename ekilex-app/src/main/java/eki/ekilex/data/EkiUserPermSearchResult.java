package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class EkiUserPermSearchResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<EkiUserPermData> ekiUserPermissions;

	private int pageNum;

	private int pageCount;

	private int totalResultCount;

	public List<EkiUserPermData> getEkiUserPermissions() {
		return ekiUserPermissions;
	}

	public void setEkiUserPermissions(List<EkiUserPermData> ekiUserPermissions) {
		this.ekiUserPermissions = ekiUserPermissions;
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

	public int getTotalResultCount() {
		return totalResultCount;
	}

	public void setTotalResultCount(int totalResultCount) {
		this.totalResultCount = totalResultCount;
	}
}
