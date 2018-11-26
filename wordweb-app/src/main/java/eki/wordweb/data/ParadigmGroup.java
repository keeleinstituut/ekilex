package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class ParadigmGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String name;

	private List<ParadigmGroup> groups;

	private List<Form> forms1;

	private List<Form> forms2;

	private boolean formsExist;

	private boolean primaryFormsExist;

	private boolean groupsExist;

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

	public boolean isFormsExist() {
		return formsExist;
	}

	public void setFormsExist(boolean formsExist) {
		this.formsExist = formsExist;
	}

	public boolean isPrimaryFormsExist() {
		return primaryFormsExist;
	}

	public void setPrimaryFormsExist(boolean primaryFormsExist) {
		this.primaryFormsExist = primaryFormsExist;
	}

	public boolean isGroupsExist() {
		return groupsExist;
	}

	public void setGroupsExist(boolean groupsExist) {
		this.groupsExist = groupsExist;
	}

}
