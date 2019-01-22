package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public abstract class WordTypeData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> wordTypeCodes;

	private List<Classifier> wordTypes;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean abbreviationWord;

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

	public boolean isAbbreviationWord() {
		return abbreviationWord;
	}

	public void setAbbreviationWord(boolean abbreviationWord) {
		this.abbreviationWord = abbreviationWord;
	}

}
