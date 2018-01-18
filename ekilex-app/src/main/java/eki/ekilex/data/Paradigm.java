package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Paradigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long paradigmId;

	private List<Form> forms;

	private List<FormRelation> formRelations;

	public Long getParadigmId() {
		return paradigmId;
	}

	public void setParadigmId(Long paradigmId) {
		this.paradigmId = paradigmId;
	}

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

	public List<FormRelation> getFormRelations() {
		return formRelations;
	}

	public void setFormRelations(List<FormRelation> formRelations) {
		this.formRelations = formRelations;
	}

}
