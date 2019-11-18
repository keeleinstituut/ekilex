package eki.ekilex.data.imp;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import eki.common.data.AbstractDataObject;

@JsonIgnoreProperties({"id"})
public class Paradigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	@JsonProperty("word_id")
	private Long wordId;

	@JsonProperty("example")
	private String example;

	@JsonProperty("inflection_type_nr")
	private String inflectionTypeNr;

	@JsonProperty("inflection_type")
	private String inflectionType;

	@JsonProperty("is_secondary")
	private boolean secondary;

	@JsonProperty("form")
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

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
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
