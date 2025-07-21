package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class TermMeaningWord extends AbstractDataObject implements DecoratedWordType {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private String wordValuePrese;

	private Integer homonymNr;

	private String lang;

	private String[] wordTypeCodes;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean foreign;

	private boolean matchingWord;

	private boolean mostPreferred;

	private boolean leastPreferred;

	private boolean isPublic;

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
