package eki.ekilex.data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import eki.common.util.LocalDateTimeDeserialiser;

@JsonInclude(Include.NON_EMPTY)
public class Word extends AbstractGrantEntity implements DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private String wordValuePrese;

	private Integer homonymNr;

	private String lang;

	private String displayMorphCode;

	private String genderCode;

	private String aspectCode;

	private String vocalForm;

	private String morphophonoForm;

	private String morphComment;

	private Integer regYear;

	private String wordFrequency;

	private String[] wordTypeCodes;

	private List<Classifier> wordTypes;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean foreign;

	private boolean isWordPublic;

	private Boolean lexemesArePublic;

	private List<String> tags;

	private List<String> lexemesValueStateLabels;

	private List<String> lexemesTagNames;

	private List<String> datasetCodes;

	private List<WordForum> forums;

	private List<WordRelation> relations;

	private List<WordGroup> groups;

	private List<WordEtym> etymology;

	private List<Paradigm> paradigms;

	private List<Freeform> freeforms;

	private WordOdRecommendation wordOdRecommendation;

	private List<WordOdUsage> wordOdUsages;

	private WordOdMorph wordOdMorph;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserialiser.class)
	private LocalDateTime lastActivityEventOn;

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

	public WordOdRecommendation getWordOdRecommendation() {
		return wordOdRecommendation;
	}

	public void setWordOdRecommendation(WordOdRecommendation wordOdRecommendation) {
		this.wordOdRecommendation = wordOdRecommendation;
	}

	public List<WordOdUsage> getWordOdUsages() {
		return wordOdUsages;
	}

	public void setWordOdUsages(List<WordOdUsage> wordOdUsages) {
		this.wordOdUsages = wordOdUsages;
	}

	public WordOdMorph getWordOdMorph() {
		return wordOdMorph;
	}

	public void setWordOdMorph(WordOdMorph wordOdMorph) {
		this.wordOdMorph = wordOdMorph;
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
