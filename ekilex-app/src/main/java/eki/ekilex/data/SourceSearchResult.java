package eki.ekilex.data;

import java.util.List;

public class SourceSearchResult extends PagingResult {

	private static final long serialVersionUID = 1L;

	private List<Source> sources;

	private int resultCount;

	private boolean resultExist;

	public List<Source> getSources() {
		return sources;
	}

	public void setSources(List<Source> sources) {
		this.sources = sources;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public boolean isResultExist() {
		return resultExist;
	}

	public void setResultExist(boolean resultExist) {
		this.resultExist = resultExist;
	}

}
