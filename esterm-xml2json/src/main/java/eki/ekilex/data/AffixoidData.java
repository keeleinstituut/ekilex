package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class AffixoidData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String wordOrigValue;

	private String wordCleanValue;

	private String affixoidWordTypeCode;

	private boolean prefixoid;

	private boolean suffixoid;

	public AffixoidData(String wordOrigValue, String wordCleanValue, String affixoidWordTypeCode, boolean prefixoid, boolean suffixoid) {
		this.wordOrigValue = wordOrigValue;
		this.wordCleanValue = wordCleanValue;
		this.affixoidWordTypeCode = affixoidWordTypeCode;
		this.prefixoid = prefixoid;
		this.suffixoid = suffixoid;
	}

	public String getWordOrigValue() {
		return wordOrigValue;
	}

	public String getWordCleanValue() {
		return wordCleanValue;
	}

	public String getAffixoidWordTypeCode() {
		return affixoidWordTypeCode;
	}

	public boolean isPrefixoid() {
		return prefixoid;
	}

	public boolean isSuffixoid() {
		return suffixoid;
	}
}
