package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class CollocRelGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String relGroupCode;

	private Classifier relGroup;

	private List<Colloc> collocations;

	private List<DisplayColloc> displayCollocs;

	private List<String> allUsageValues;

	public String getRelGroupCode() {
		return relGroupCode;
	}

	public void setRelGroupCode(String relGroupCode) {
		this.relGroupCode = relGroupCode;
	}

	public Classifier getRelGroup() {
		return relGroup;
	}

	public void setRelGroup(Classifier relGroup) {
		this.relGroup = relGroup;
	}

	public List<Colloc> getCollocations() {
		return collocations;
	}

	public void setCollocations(List<Colloc> collocations) {
		this.collocations = collocations;
	}

	public List<DisplayColloc> getDisplayCollocs() {
		return displayCollocs;
	}

	public void setDisplayCollocs(List<DisplayColloc> displayCollocs) {
		this.displayCollocs = displayCollocs;
	}

	public List<String> getAllUsageValues() {
		return allUsageValues;
	}

	public void setAllUsageValues(List<String> allUsageValues) {
		this.allUsageValues = allUsageValues;
	}

}
