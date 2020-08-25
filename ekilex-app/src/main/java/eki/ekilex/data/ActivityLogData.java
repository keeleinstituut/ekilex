package eki.ekilex.data;

public class ActivityLogData extends ActivityLog {

	private static final long serialVersionUID = 1L;

	private WordLexemeMeaningIds prevWlmIds;

	private WordLexemeMeaningIds currWlmIds;

	public WordLexemeMeaningIds getPrevWlmIds() {
		return prevWlmIds;
	}

	public void setPrevWlmIds(WordLexemeMeaningIds prevWlmIds) {
		this.prevWlmIds = prevWlmIds;
	}

	public WordLexemeMeaningIds getCurrWlmIds() {
		return currWlmIds;
	}

	public void setCurrWlmIds(WordLexemeMeaningIds currWlmIds) {
		this.currWlmIds = currWlmIds;
	}

}
