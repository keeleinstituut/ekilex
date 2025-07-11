package eki.wordweb.data.od;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;
import eki.wordweb.data.DecoratedWordType;

public class OdWord extends AbstractDataObject implements DecoratedWordType {

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

	private WordOdMorph wordOdMorph;

	private List<WordOdUsage> wordOdUsages;

	private WordOdRecommend wordOdRecommend;

	private List<OdLexemeMeaning> lexemeMeanings;

	private List<OdWordRelationGroup> wordRelationGroups;

	private List<OdWordRelationGroup> primaryWordRelationGroups;

	private List<OdWordRelationGroup> secondaryWordRelationGroups;

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

	public WordOdMorph getWordOdMorph() {
		return wordOdMorph;
	}

	public void setWordOdMorph(WordOdMorph wordOdMorph) {
		this.wordOdMorph = wordOdMorph;
	}

	public List<WordOdUsage> getWordOdUsages() {
		return wordOdUsages;
	}

	public void setWordOdUsages(List<WordOdUsage> wordOdUsages) {
		this.wordOdUsages = wordOdUsages;
	}

	public WordOdRecommend getWordOdRecommend() {
		return wordOdRecommend;
	}

	public void setWordOdRecommend(WordOdRecommend wordOdRecommend) {
		this.wordOdRecommend = wordOdRecommend;
	}

	public List<OdLexemeMeaning> getLexemeMeanings() {
		return lexemeMeanings;
	}

	public void setLexemeMeanings(List<OdLexemeMeaning> lexemeMeanings) {
		this.lexemeMeanings = lexemeMeanings;
	}

	public List<OdWordRelationGroup> getWordRelationGroups() {
		return wordRelationGroups;
	}

	public void setWordRelationGroups(List<OdWordRelationGroup> wordRelationGroups) {
		this.wordRelationGroups = wordRelationGroups;
	}

	public List<OdWordRelationGroup> getPrimaryWordRelationGroups() {
		return primaryWordRelationGroups;
	}

	public void setPrimaryWordRelationGroups(List<OdWordRelationGroup> primaryWordRelationGroups) {
		this.primaryWordRelationGroups = primaryWordRelationGroups;
	}

	public List<OdWordRelationGroup> getSecondaryWordRelationGroups() {
		return secondaryWordRelationGroups;
	}

	public void setSecondaryWordRelationGroups(List<OdWordRelationGroup> secondaryWordRelationGroups) {
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
