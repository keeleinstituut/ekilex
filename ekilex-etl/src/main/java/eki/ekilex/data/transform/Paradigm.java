package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Paradigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String inflectionTypeNr;

	private Integer homonymNr;

	private boolean isSecondary;

	private List<String> formValues;

	private List<Form> forms;

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

	public String getInflectionTypeNr() {
		return inflectionTypeNr;
	}

	public void setInflectionTypeNr(String inflectionTypeNr) {
		this.inflectionTypeNr = inflectionTypeNr;
	}

	public boolean isSecondary() {
		return isSecondary;
	}

	public void setSecondary(boolean secondary) {
		isSecondary = secondary;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}
}
