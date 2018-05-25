package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Paradigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long paradigmId;

	private String title;

	private List<Form> forms;

	private List<FormPair> compactForms;

	public Long getParadigmId() {
		return paradigmId;
	}

	public void setParadigmId(Long paradigmId) {
		this.paradigmId = paradigmId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

	public List<FormPair> getCompactForms() {
		return compactForms;
	}

	public void setCompactForms(List<FormPair> compactForms) {
		this.compactForms = compactForms;
	}

}
