package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermDetails extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Meaning> meanings;

	public List<Meaning> getMeanings() {
		return meanings;
	}

	public void setMeanings(List<Meaning> meanings) {
		this.meanings = meanings;
	}

}
