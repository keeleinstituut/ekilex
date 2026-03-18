package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordSuggestionPage extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<WordSuggestion> wordSuggestions;

	private int totalCount;

	private int totalPages;

	private int currentPage;

	private int startPage;

	private int endPage;

	private boolean showPaging;

	public List<WordSuggestion> getWordSuggestions() {
		return wordSuggestions;
	}

	public void setWordSuggestions(List<WordSuggestion> wordSuggestions) {
		this.wordSuggestions = wordSuggestions;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public boolean isShowPaging() {
		return showPaging;
	}

	public void setShowPaging(boolean showPaging) {
		this.showPaging = showPaging;
	}
}
