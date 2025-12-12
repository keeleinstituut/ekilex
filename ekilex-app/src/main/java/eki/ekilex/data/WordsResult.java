package eki.ekilex.data;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
@Schema(description = "Search result object containing a list of matching words and total count")
public class WordsResult extends PagingResult {

	private static final long serialVersionUID = 1L;
	@Schema(
			description = "Total number of matching words found for the search query",
			example = "1"
	)
	private int totalCount;
	@ArraySchema(
			arraySchema = @Schema(description = "List of matching word objects"),
			schema = @Schema(implementation = eki.ekilex.data.Word.class)
	)
	private List<Word> words;

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<Word> getWords() {
		return words;
	}

	public void setWords(List<Word> words) {
		this.words = words;
	}
}
