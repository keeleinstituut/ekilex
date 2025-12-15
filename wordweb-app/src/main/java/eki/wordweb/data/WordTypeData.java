package eki.wordweb.data;

import java.util.List;

import eki.common.data.Classifier;

public abstract class WordTypeData extends AbstractPublishingEntity implements LangType, DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	// TODO actually, change to wordValue. value is too ambiguous
	private String value;

	private String valuePrese;

	private String valueAsWord;

	private Integer homonymNr;

	private boolean homonymExists;

	private String lang;

	private String displayMorphCode;

	private Classifier displayMorph;

	private String genderCode;

	private Classifier gender;

	private String aspectCode;

	private Classifier aspect;

	private Integer regYear;

	private List<String> wordTypeCodes;

	private List<Classifier> wordTypes;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean foreignWord;

	private boolean abbreviationWord;

	private boolean incorrectWordForm;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
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

	public String getValueAsWord() {
		return valueAsWord;
	}

	public void setValueAsWord(String valueAsWord) {
		this.valueAsWord = valueAsWord;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public boolean isHomonymExists() {
		return homonymExists;
	}

	public void setHomonymExists(boolean homonymExists) {
		this.homonymExists = homonymExists;
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

	public Integer getRegYear() {
		return regYear;
	}

	public void setRegYear(Integer regYear) {
		this.regYear = regYear;
	}

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

	public boolean isIncorrectWordForm() {
		return incorrectWordForm;
	}

	public void setIncorrectWordForm(boolean incorrectWordForm) {
		this.incorrectWordForm = incorrectWordForm;
	}

}
