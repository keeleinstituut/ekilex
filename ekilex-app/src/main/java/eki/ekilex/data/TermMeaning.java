package eki.ekilex.data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import eki.common.data.AbstractDataObject;

@Schema(
		description = "Represents a single meaning with all associated words/terms that share this meaning. "
)
public class TermMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Schema(
			description = "Unique identifier for this meaning",
			example = "33091"
	)
	private Long meaningId;

	@Schema(
			description = "List of domain classifiers indicating subject domains or fields this meaning belongs to. " +
					"Can be null if no specific domain is assigned.",
			nullable = true,
			example = "null"
	)
	private List<Classifier> meaningDomains;

	@Schema(
			description = "Array of TermMeaningWord objects representing all words/terms that share this meaning, organized by language"
	)
	private List<TermMeaningWord> meaningWords;

	@Schema(
			description = "Boolean indicating whether this meaning has associated words/terms",
			example = "true"
	)
	private boolean meaningWordsExist;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public List<Classifier> getMeaningDomains() {
		return meaningDomains;
	}

	public void setMeaningDomains(List<Classifier> meaningDomains) {
		this.meaningDomains = meaningDomains;
	}

	public List<TermMeaningWord> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<TermMeaningWord> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public boolean isMeaningWordsExist() {
		return meaningWordsExist;
	}

	public void setMeaningWordsExist(boolean meaningWordsExist) {
		this.meaningWordsExist = meaningWordsExist;
	}

}
