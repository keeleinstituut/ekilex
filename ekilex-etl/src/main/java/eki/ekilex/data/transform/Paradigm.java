package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Paradigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String wordClass;

	private String inflectionTypeNr;

	private String inflectionType;

	private boolean isSecondary;

	private List<String> formValues;

	private List<Form> forms;

	public String getWordClass() {
		return wordClass;
	}

	public void setWordClass(String wordClass) {
		this.wordClass = wordClass;
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
		return isSecondary;
	}

	public void setSecondary(boolean isSecondary) {
		this.isSecondary = isSecondary;
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

}
