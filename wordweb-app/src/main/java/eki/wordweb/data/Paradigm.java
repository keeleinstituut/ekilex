package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Paradigm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long paradigmId;

	private String title;

	private List<ParadigmGroup> groups;

	private boolean expandable;

	//TODO will be removed later
	private List<Form> forms;

	//TODO will be removed later
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

	public List<ParadigmGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<ParadigmGroup> groups) {
		this.groups = groups;
	}

	public boolean isExpandable() {
		return expandable;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
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
