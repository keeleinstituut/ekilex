package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class EkiUserPermSearchResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<EkiUserPermData> ekiUserPermissions;

	private int pageNum;

	private int totalPages;

	private int totalItems;

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

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}
}
