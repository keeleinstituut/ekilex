package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public abstract class WordTypeData extends AbstractDataObject implements LangType, DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String word;

	private String wordPrese;

	private String asWord;

	private String lang;

	private Integer homonymNr;

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

	@Override
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
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
