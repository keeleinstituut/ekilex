package eki.ekilex.data.migra;

import eki.common.data.AbstractDataObject;

public class Word extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String lang;

	public Word(Long id, String value, String lang) {
		this.id = id;
		this.value = value;
		this.lang = lang;
	}

	public Long getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public String getLang() {
		return lang;
	}

}
