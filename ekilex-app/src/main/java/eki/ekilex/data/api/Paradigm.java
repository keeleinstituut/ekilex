package eki.ekilex.data.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eki.common.data.AbstractDataObject;

public class Paradigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private Long id;

	private Long wordId;

	private String wordClass;

	private String comment;

	private String inflectionTypeNr;

	private String inflectionType;

	private boolean secondary;

	private List<Form> forms;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWordClass() {
		return wordClass;
	}

	public void setWordClass(String wordClass) {
		this.wordClass = wordClass;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getInflectionTypeNr() {
		return inflectionTypeNr;
	}

	public void setInflectionTypeNr(String inflectionTypeNr) {
		this.inflectionTypeNr = inflectionTypeNr;
	}

	public String getInflectionType() {
		return inflectionType;
	}

	public void setInflectionType(String inflectionType) {
		this.inflectionType = inflectionType;
	}

	public boolean isSecondary() {
		return secondary;
	}

	public void setSecondary(boolean secondary) {
		this.secondary = secondary;
	}

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

}
