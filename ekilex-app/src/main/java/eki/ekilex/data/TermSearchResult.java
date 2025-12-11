package eki.ekilex.data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		description = "Result of a terminology search containing meanings and their associated terms. " +
				"Meanings are grouped with all terms that share the same meaning across different languages."
)
public class TermSearchResult extends PagingResult {

	private static final long serialVersionUID = 1L;

	@Schema(
			description = "Total number of distinct meanings found in the search results",
			example = "1"
	)
	private int meaningCount;

	@Schema(
			description = "Total number of words/terms across all meanings in the results",
			example = "2"
	)
	private int wordCount;

	@Schema(
			description = "Number of result entries (meaning groups) returned",
			example = "1"
	)
	private int resultCount;

	@Schema(
			description = "Array of TermMeaning objects representing meanings with their associated terms. " +
					"Each meaning contains a list of words/terms that share the same semantic meaning across different languages."
	)
	private List<TermMeaning> results;

	@Schema(
			description = "Boolean indicating whether results exist for the search query",
			example = "true"
	)
	private boolean resultExist;

	@Schema(
			description = "Boolean indicating whether results are available for immediate download",
			example = "true"
	)
	private boolean resultDownloadNow;

	@Schema(
			description = "Boolean indicating whether results are available for later download",
			example = "false"
	)
	private boolean resultDownloadLater;

	public int getMeaningCount() {
		return meaningCount;
	}

	public void setMeaningCount(int meaningCount) {
		this.meaningCount = meaningCount;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public List<TermMeaning> getResults() {
		return results;
	}

	public void setResults(List<TermMeaning> results) {
		this.results = results;
	}

	public boolean isResultExist() {
		return resultExist;
	}

	public void setResultExist(boolean resultExist) {
		this.resultExist = resultExist;
	}

	public boolean isResultDownloadNow() {
		return resultDownloadNow;
	}

	public void setResultDownloadNow(boolean resultDownloadNow) {
		this.resultDownloadNow = resultDownloadNow;
	}

	public boolean isResultDownloadLater() {
		return resultDownloadLater;
	}

	public void setResultDownloadLater(boolean resultDownloadLater) {
		this.resultDownloadLater = resultDownloadLater;
	}

}
