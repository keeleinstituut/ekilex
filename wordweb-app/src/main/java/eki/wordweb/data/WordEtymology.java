package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordEtymology extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<TypeWordEtym> etymLineup;

	public List<TypeWordEtym> getEtymLineup() {
		return etymLineup;
	}

	public void setEtymLineup(List<TypeWordEtym> etymLineup) {
		this.etymLineup = etymLineup;
	}

}
