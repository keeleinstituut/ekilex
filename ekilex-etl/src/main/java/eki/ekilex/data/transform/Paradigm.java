package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Paradigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String word;

	private Integer inflectionTypeNr;

	private List<String> formValues;

	private List<Form> forms;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public List<String> getFormValues() {
		return formValues;
	}

	public void setFormValues(List<String> formValues) {
		this.formValues = formValues;
	}

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

	public Integer getInflectionTypeNr() {
		return inflectionTypeNr;
	}

	public void setInflectionTypeNr(Integer inflectionTypeNr) {
		this.inflectionTypeNr = inflectionTypeNr;
	}
}
