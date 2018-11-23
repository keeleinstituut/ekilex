package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class ParadigmGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String name;

	private List<ParadigmGroup> groups;

	private List<Form> forms1;

	private List<Form> forms2;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ParadigmGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<ParadigmGroup> groups) {
		this.groups = groups;
	}

	public List<Form> getForms1() {
		return forms1;
	}

	public void setForms1(List<Form> forms) {
		this.forms1 = forms;
	}

	public List<Form> getForms2() {
		return forms2;
	}

	public void setForms2(List<Form> forms2) {
		this.forms2 = forms2;
	}

}
