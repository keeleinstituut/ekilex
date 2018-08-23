package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class CollocationRelGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String name;

	private List<String> allUsages;

	private List<Collocation> collocations;

	private List<DisplayColloc> displayCollocs;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAllUsages() {
		return allUsages;
	}

	public void setAllUsages(List<String> allUsages) {
		this.allUsages = allUsages;
	}

	public List<Collocation> getCollocations() {
		return collocations;
	}

	public void setCollocations(List<Collocation> collocations) {
		this.collocations = collocations;
	}

	public List<DisplayColloc> getDisplayCollocs() {
		return displayCollocs;
	}

	public void setDisplayCollocs(List<DisplayColloc> displayCollocs) {
		this.displayCollocs = displayCollocs;
	}

}
