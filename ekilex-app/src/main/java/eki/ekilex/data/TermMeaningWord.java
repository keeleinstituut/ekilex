package eki.ekilex.data;

import io.swagger.v3.oas.annotations.media.Schema;
import eki.common.data.AbstractDataObject;

@Schema(
		description = "Represents a single word/term that is associated with a specific meaning. " +
				"Contains linguistic properties, language information, and metadata about the term."
)
public class TermMeaningWord extends AbstractDataObject implements DecoratedWordType {

	private static final long serialVersionUID = 1L;

	@Schema(
			description = "Unique identifier for this word in the database",
			example = "175467"
	)
	private Long wordId;

	@Schema(
			description = "The actual word/term value as stored in the database",
			example = "jääkarussell"
	)
	private String wordValue;

	@Schema(
			description = "Presentation form of the word/term, may include formatting or linguistic markup. " +
					"Can contain XML tags for special formatting like stress marks.",
			example = "jääkarussell"
	)
	private String wordValuePrese;

	@Schema(
			description = "Homonym number if multiple entries exist for the same word value. ",
			example = "1"
	)
	private Integer homonymNr;

	@Schema(
			description = "Language code for this word/term (e.g., 'est' for Estonian, 'eng' for English, 'rus' for Russian)",
			example = "est"
	)
	private String lang;

	private String[] wordTypeCodes;

	private boolean prefixoid;

	private boolean suffixoid;

	@Schema(
			description = "Boolean indicating whether this term is a foreign word",
			example = "false"
	)
	private boolean foreign;

	@Schema(
			description = "Boolean indicating whether this term matches the original search query. " +
					"True if this is one of the terms the user searched for, false if it's a related/translation term.",
			example = "true"
	)
	private boolean matchingWord;

	private boolean mostPreferred;

	private boolean leastPreferred;

	private boolean isPublic;

	@Schema(
			description = "Array of dataset codes indicating which terminology databases this term belongs to. ",
			example = "[\"eki\"]"
	)
	private String[] datasetCodes;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	@Override
	public String getWordValue() {
		return wordValue;
	}

	@Override
	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	@Override
	public String getWordValuePrese() {
		return wordValuePrese;
	}

	@Override
	public void setWordValuePrese(String wordValuePrese) {
		this.wordValuePrese = wordValuePrese;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public String[] getWordTypeCodes() {
		return wordTypeCodes;
	}

	@Override
	public void setWordTypeCodes(String[] wordTypeCodes) {
		this.wordTypeCodes = wordTypeCodes;
	}

	@Override
	public boolean isPrefixoid() {
		return prefixoid;
	}

	@Override
	public void setPrefixoid(boolean prefixoid) {
		this.prefixoid = prefixoid;
	}

	@Override
	public boolean isSuffixoid() {
		return suffixoid;
	}

	@Override
	public void setSuffixoid(boolean suffixoid) {
		this.suffixoid = suffixoid;
	}

	@Override
	public boolean isForeign() {
		return foreign;
	}

	@Override
	public void setForeign(boolean foreign) {
		this.foreign = foreign;
	}

	public boolean isMatchingWord() {
		return matchingWord;
	}

	public void setMatchingWord(boolean matchingWord) {
		this.matchingWord = matchingWord;
	}

	public boolean isMostPreferred() {
		return mostPreferred;
	}

	public void setMostPreferred(boolean mostPreferred) {
		this.mostPreferred = mostPreferred;
	}

	public boolean isLeastPreferred() {
		return leastPreferred;
	}

	public void setLeastPreferred(boolean leastPreferred) {
		this.leastPreferred = leastPreferred;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String[] getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(String[] datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

}
