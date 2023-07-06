package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class TermFreeform extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String lang;

	private Boolean publicity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Boolean getPublicity() {
		return publicity;
	}

	public void setPublicity(Boolean publicity) {
		this.publicity = publicity;
	}
}
