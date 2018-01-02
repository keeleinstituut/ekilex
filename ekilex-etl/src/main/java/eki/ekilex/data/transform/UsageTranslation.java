package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class UsageTranslation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String lang;

	private String value;

	private List<String> originalTokens;

	private List<String> lemmatisedTokens;

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<String> getOriginalTokens() {
		return originalTokens;
	}

	public void setOriginalTokens(List<String> originalTokens) {
		this.originalTokens = originalTokens;
	}

	public List<String> getLemmatisedTokens() {
		return lemmatisedTokens;
	}

	public void setLemmatisedTokens(List<String> lemmatisedTokens) {
		this.lemmatisedTokens = lemmatisedTokens;
	}

}
