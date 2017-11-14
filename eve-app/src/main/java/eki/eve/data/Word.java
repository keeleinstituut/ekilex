package eki.eve.data;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

import java.util.function.Consumer;

public class Word extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "form_id")
	private Long formId;

	@Column(name = "word")
	private String value;

	@Column(name = "homonym_nr")
	private Integer homonymNumber;

	@Column(name = "lang")
	private String language;

	public Word() {
	}

	public Word(Consumer<Word> builder) {
		builder.accept(this);
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getHomonymNumber() {
		return homonymNumber;
	}

	public void setHomonymNumber(Integer homonymNumber) {
		this.homonymNumber = homonymNumber;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
