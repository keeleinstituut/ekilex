package eki.wordweb.data.os;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;
import eki.wordweb.data.DecoratedWordType;

public class OsWord extends AbstractDataObject implements DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long meaningId;

	private String value;

	private String valuePrese;

	private Integer homonymNr;

	private String displayMorphCode;

	private Classifier displayMorph;

	private List<String> wordTypeCodes;

	private List<Classifier> wordTypes;

	private WordOsMorph wordOsMorph;

	private List<WordOsUsage> wordOsUsages;

	private WordOsRecommend wordOsRecommend;

	private List<OsLexemeMeaning> lexemeMeanings;

	private List<OsWordRelationGroup> wordRelationGroups;

	private List<OsWordRelationGroup> primaryWordRelationGroups;

	private List<OsWordRelationGroup> secondaryWordRelationGroups;

	private String meaningWordsWrapup;

	private String definitionsWrapup;

	private String searchUri;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean foreignWord;

	private boolean abbreviationWord;

	private boolean selected;

	private boolean lexemeMeaningsContentExist;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getDisplayMorphCode() {
		return displayMorphCode;
	}

	public void setDisplayMorphCode(String displayMorphCode) {
		this.displayMorphCode = displayMorphCode;
	}

	public Classifier getDisplayMorph() {
		return displayMorph;
	}

	public void setDisplayMorph(Classifier displayMorph) {
		this.displayMorph = displayMorph;
	}

	@Override
	public List<String> getWordTypeCodes() {
		return wordTypeCodes;
	}

	public void setWordTypeCodes(List<String> wordTypeCodes) {
		this.wordTypeCodes = wordTypeCodes;
	}

	public List<Classifier> getWordTypes() {
		return wordTypes;
	}

	public void setWordTypes(List<Classifier> wordTypes) {
		this.wordTypes = wordTypes;
	}

	public WordOsMorph getWordOsMorph() {
		return wordOsMorph;
	}

	public void setWordOsMorph(WordOsMorph wordOsMorph) {
		this.wordOsMorph = wordOsMorph;
	}

	public List<WordOsUsage> getWordOsUsages() {
		return wordOsUsages;
	}

	public void setWordOsUsages(List<WordOsUsage> wordOsUsages) {
		this.wordOsUsages = wordOsUsages;
	}

	public WordOsRecommend getWordOsRecommend() {
		return wordOsRecommend;
	}

	public void setWordOsRecommend(WordOsRecommend wordOsRecommend) {
		this.wordOsRecommend = wordOsRecommend;
	}

	public List<OsLexemeMeaning> getLexemeMeanings() {
		return lexemeMeanings;
	}

	public void setLexemeMeanings(List<OsLexemeMeaning> lexemeMeanings) {
		this.lexemeMeanings = lexemeMeanings;
	}

	public List<OsWordRelationGroup> getWordRelationGroups() {
		return wordRelationGroups;
	}

	public void setWordRelationGroups(List<OsWordRelationGroup> wordRelationGroups) {
		this.wordRelationGroups = wordRelationGroups;
	}

	public List<OsWordRelationGroup> getPrimaryWordRelationGroups() {
		return primaryWordRelationGroups;
	}

	public void setPrimaryWordRelationGroups(List<OsWordRelationGroup> primaryWordRelationGroups) {
		this.primaryWordRelationGroups = primaryWordRelationGroups;
	}

	public List<OsWordRelationGroup> getSecondaryWordRelationGroups() {
		return secondaryWordRelationGroups;
	}

	public void setSecondaryWordRelationGroups(List<OsWordRelationGroup> secondaryWordRelationGroups) {
		this.secondaryWordRelationGroups = secondaryWordRelationGroups;
	}

	public String getMeaningWordsWrapup() {
		return meaningWordsWrapup;
	}

	public void setMeaningWordsWrapup(String meaningWordsWrapup) {
		this.meaningWordsWrapup = meaningWordsWrapup;
	}

	public String getDefinitionsWrapup() {
		return definitionsWrapup;
	}

	public void setDefinitionsWrapup(String definitionsWrapup) {
		this.definitionsWrapup = definitionsWrapup;
	}

	public String getSearchUri() {
		return searchUri;
	}

	public void setSearchUri(String searchUri) {
		this.searchUri = searchUri;
	}

	@Override
	public boolean isPrefixoid() {
		return prefixoid;
	}

	public void setPrefixoid(boolean prefixoid) {
		this.prefixoid = prefixoid;
	}

	@Override
	public boolean isSuffixoid() {
		return suffixoid;
	}

	public void setSuffixoid(boolean suffixoid) {
		this.suffixoid = suffixoid;
	}

	@Override
	public boolean isForeignWord() {
		return foreignWord;
	}

	public void setForeignWord(boolean foreignWord) {
		this.foreignWord = foreignWord;
	}

	@Override
	public boolean isAbbreviationWord() {
		return abbreviationWord;
	}

	public void setAbbreviationWord(boolean abbreviationWord) {
		this.abbreviationWord = abbreviationWord;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isLexemeMeaningsContentExist() {
		return lexemeMeaningsContentExist;
	}

	public void setLexemeMeaningsContentExist(boolean lexemeMeaningsContentExist) {
		this.lexemeMeaningsContentExist = lexemeMeaningsContentExist;
	}

}
