package eki.ekilex.data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.util.LocalDateTimeDeserialiser;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a lexical word entry returned in search results")
@JsonInclude(Include.NON_EMPTY)
public class Word extends AbstractGrantEntity implements DecoratedWordType {

	private static final long serialVersionUID = 1L;
	@Schema(description = "Unique identifier of the word", example = "182736")
	private Long wordId;
	@Schema(description = "The actual word string", example = "kobar")
	private String wordValue;
	@Schema(description = "The presentational value for the word. Can include <eki-...> - markups.", example = "kobar")
	private String wordValuePrese;
	@Schema(description = "Sequence number of homonyms for this word", example = "1")
	private Integer homonymNr;
	@Schema(description = "Language code of the word", example = "est")
	private String lang;
	@Schema(hidden = true)
	private String displayMorphCode;
	@Schema(hidden = true)
	private String genderCode;
	@Schema(hidden = true)
	private String aspectCode;
	@Schema(hidden = true)
	private String vocalForm;
	@Schema(example = "kobar")
	private String morphophonoForm;
	@Schema(hidden = true)
	private String morphComment;
	@Schema(hidden = true)
	private Integer regYear;
	@Schema(hidden = true)
	private String wordFrequency;
	@Schema(hidden = true)
	private String[] wordTypeCodes;
	@Schema(hidden = true)
	private List<Classifier> wordTypes;
	@Schema(example = "false")
	private boolean prefixoid;
	@Schema(example = "false")
	private boolean suffixoid;
	@Schema(description = "Whether the word is foreign or not", example = "false")
	private boolean foreign;
	@Schema(hidden = true)
	private boolean isWordPublic;
	@Schema(hidden = true)
	private Boolean lexemesArePublic;
	@Schema(hidden = true)
	private List<String> tags;
	@Schema(hidden = true)
	private List<String> lexemesValueStateLabels;
	@Schema(description = "Active tags on the lexeme", example = "[\"ÕSi sõna\"]")
	private List<String> lexemesTagNames;
	@Schema(description = "Datasets where this word is included", example = "[\n" +
			"                \"ait\",\n" +
			"                \"aso\",\n" +
			"                \"bks\",\n" +
			"                \"eiops\",\n" +
			"                \"eki\",\n" +
			"                \"esterm\",\n" +
			"                \"ety\",\n" +
			"                \"gal\",\n" +
			"                \"kkt\",\n" +
			"                \"kool_KV\",\n" +
			"                \"les\",\n" +
			"                \"mer\",\n" +
			"                \"mil\"\n" +
			"            ]")
	private List<String> datasetCodes;
	@Schema(hidden = true)
	private List<WordForum> forums;
	@Schema(hidden = true)
	private List<WordRelation> relations;
	@Schema(hidden = true)
	private List<WordGroup> groups;
	@Schema(description = "Etymological details about the word")
	private List<WordEtym> etymology;
	@Schema(description = "All paradigms and their forms, ordered by inflection type")
	private List<Paradigm> paradigms;
	@Schema(hidden = true)
	private List<Freeform> freeforms;
	@Schema(hidden = true)
	private List<WordEkiRecommendation> wordEkiRecommendations;
	@Schema(description = "Examples of word usages in the dictionary Õigekeelsussõnaraamat")
	private List<WordOsUsage> wordOsUsages;

	private WordOsMorph wordOsMorph;
	@Schema(example = "2025-07-04T13:37:10.838442")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime lastActivityEventOn;
	@Schema(hidden = true)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime manualEventOn;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public String getWordValuePrese() {
		return wordValuePrese;
	}

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

	public String getDisplayMorphCode() {
		return displayMorphCode;
	}

	public void setDisplayMorphCode(String displayMorphCode) {
		this.displayMorphCode = displayMorphCode;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	public String getAspectCode() {
		return aspectCode;
	}

	public void setAspectCode(String aspectCode) {
		this.aspectCode = aspectCode;
	}

	public String getVocalForm() {
		return vocalForm;
	}

	public void setVocalForm(String vocalForm) {
		this.vocalForm = vocalForm;
	}

	public String getMorphophonoForm() {
		return morphophonoForm;
	}

	public void setMorphophonoForm(String morphophonoForm) {
		this.morphophonoForm = morphophonoForm;
	}

	public String getMorphComment() {
		return morphComment;
	}

	public void setMorphComment(String morphComment) {
		this.morphComment = morphComment;
	}

	public Integer getRegYear() {
		return regYear;
	}

	public void setRegYear(Integer regYear) {
		this.regYear = regYear;
	}

	public String getWordFrequency() {
		return wordFrequency;
	}

	public void setWordFrequency(String wordFrequency) {
		this.wordFrequency = wordFrequency;
	}

	public String[] getWordTypeCodes() {
		return wordTypeCodes;
	}

	public void setWordTypeCodes(String[] wordTypeCodes) {
		this.wordTypeCodes = wordTypeCodes;
	}

	public List<Classifier> getWordTypes() {
		return wordTypes;
	}

	public void setWordTypes(List<Classifier> wordTypes) {
		this.wordTypes = wordTypes;
	}

	public boolean isPrefixoid() {
		return prefixoid;
	}

	public void setPrefixoid(boolean prefixoid) {
		this.prefixoid = prefixoid;
	}

	public boolean isSuffixoid() {
		return suffixoid;
	}

	public void setSuffixoid(boolean suffixoid) {
		this.suffixoid = suffixoid;
	}

	public boolean isForeign() {
		return foreign;
	}

	public void setForeign(boolean foreign) {
		this.foreign = foreign;
	}

	public boolean isWordPublic() {
		return isWordPublic;
	}

	public void setWordPublic(boolean isWordPublic) {
		this.isWordPublic = isWordPublic;
	}

	public Boolean getLexemesArePublic() {
		return lexemesArePublic;
	}

	public void setLexemesArePublic(Boolean lexemesArePublic) {
		this.lexemesArePublic = lexemesArePublic;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getLexemesValueStateLabels() {
		return lexemesValueStateLabels;
	}

	public void setLexemesValueStateLabels(List<String> lexemesValueStateLabels) {
		this.lexemesValueStateLabels = lexemesValueStateLabels;
	}

	public List<String> getLexemesTagNames() {
		return lexemesTagNames;
	}

	public void setLexemesTagNames(List<String> lexemesTagNames) {
		this.lexemesTagNames = lexemesTagNames;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

	public List<WordForum> getForums() {
		return forums;
	}

	public void setForums(List<WordForum> forums) {
		this.forums = forums;
	}

	public List<WordRelation> getRelations() {
		return relations;
	}

	public void setRelations(List<WordRelation> relations) {
		this.relations = relations;
	}

	public List<WordGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<WordGroup> groups) {
		this.groups = groups;
	}

	public List<WordEtym> getEtymology() {
		return etymology;
	}

	public void setEtymology(List<WordEtym> etymology) {
		this.etymology = etymology;
	}

	public List<Paradigm> getParadigms() {
		return paradigms;
	}

	public void setParadigms(List<Paradigm> paradigms) {
		this.paradigms = paradigms;
	}

	public List<Freeform> getFreeforms() {
		return freeforms;
	}

	public void setFreeforms(List<Freeform> freeforms) {
		this.freeforms = freeforms;
	}

	public List<WordEkiRecommendation> getWordEkiRecommendations() {
		return wordEkiRecommendations;
	}

	public void setWordEkiRecommendations(List<WordEkiRecommendation> wordEkiRecommendations) {
		this.wordEkiRecommendations = wordEkiRecommendations;
	}

	public List<WordOsUsage> getWordOsUsages() {
		return wordOsUsages;
	}

	public void setWordOsUsages(List<WordOsUsage> wordOsUsages) {
		this.wordOsUsages = wordOsUsages;
	}

	public WordOsMorph getWordOsMorph() {
		return wordOsMorph;
	}

	public void setWordOsMorph(WordOsMorph wordOsMorph) {
		this.wordOsMorph = wordOsMorph;
	}

	public LocalDateTime getLastActivityEventOn() {
		return lastActivityEventOn;
	}

	public void setLastActivityEventOn(LocalDateTime lastActivityEventOn) {
		this.lastActivityEventOn = lastActivityEventOn;
	}

	public LocalDateTime getManualEventOn() {
		return manualEventOn;
	}

	public void setManualEventOn(LocalDateTime manualEventOn) {
		this.manualEventOn = manualEventOn;
	}

}
