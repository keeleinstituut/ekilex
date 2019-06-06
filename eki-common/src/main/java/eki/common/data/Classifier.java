package eki.common.data;

import java.util.List;

public class Classifier extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String name;

	private String origin;

	private String parent;

	private String code;

	private String value;

	private String lang;

	private List<String> datasets;

	public Classifier() {
	}

	public Classifier(String name, String origin, String parent, String code, String value, String lang) {
		this.name = name;
		this.origin = origin;
		this.parent = parent;
		this.code = code;
		this.value = value;
		this.lang = lang;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

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

	public List<String> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<String> datasets) {
		this.datasets = datasets;
	}
}
