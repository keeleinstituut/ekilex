package eki.wordweb.data;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class WordTypeData extends AbstractDataObject implements LangType, DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String word;

	private String wordPrese;

	private String asWord;

	private Integer homonymNr;

	private String lang;

	private String displayMorphCode;

	private Classifier displayMorph;

	private String genderCode;

	private Classifier gender;

	private String aspectCode;

	private Classifier aspect;

	private List<String> wordTypeCodes;

	private List<Classifier> wordTypes;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean abbreviationWord;

	private boolean foreignWord;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	@Override
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	@Override
	public String getWordPrese() {
		return wordPrese;
	}

	public void setWordPrese(String wordPrese) {
		this.wordPrese = wordPrese;
	}

	public String getAsWord() {
		return asWord;
	}

	public void setAsWord(String asWord) {
		this.asWord = asWord;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	@Override
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

	public Classifier getDisplayMorph() {
		return displayMorph;
	}

	public void setDisplayMorph(Classifier displayMorph) {
		this.displayMorph = displayMorph;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	public Classifier getGender() {
		return gender;
	}

	public void setGender(Classifier gender) {
		this.gender = gender;
	}

	public String getAspectCode() {
		return aspectCode;
	}

	public void setAspectCode(String aspectCode) {
		this.aspectCode = aspectCode;
	}

	public Classifier getAspect() {
		return aspect;
	}

	public void setAspect(Classifier aspect) {
		this.aspect = aspect;
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

	public boolean isAbbreviationWord() {
		return abbreviationWord;
	}

	public void setAbbreviationWord(boolean abbreviationWord) {
		this.abbreviationWord = abbreviationWord;
	}

	@Override
	public boolean isForeignWord() {
		return foreignWord;
	}

	public void setForeignWord(boolean foreignWord) {
		this.foreignWord = foreignWord;
	}
}
