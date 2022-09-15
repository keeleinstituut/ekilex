package eki.ekilex.data.api;

import java.math.BigDecimal;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordSynCandidate extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String lang;

	private BigDecimal weight;

	private List<String> posCodes;

	private List<TextWithSource> definitions;

	private List<TextWithSource> usages;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public List<String> getPosCodes() {
		return posCodes;
	}

	public void setPosCodes(List<String> posCodes) {
		this.posCodes = posCodes;
	}

	public List<TextWithSource> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<TextWithSource> definitions) {
		this.definitions = definitions;
	}

	public List<TextWithSource> getUsages() {
		return usages;
	}

	public void setUsages(List<TextWithSource> usages) {
		this.usages = usages;
	}

}
