package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class DatasetHomeData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Dataset dataset;

	private List<Character> firstLetters;

	private boolean validDataset;

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public List<Character> getFirstLetters() {
		return firstLetters;
	}

	public void setFirstLetters(List<Character> firstLetters) {
		this.firstLetters = firstLetters;
	}

	public boolean isValidDataset() {
		return validDataset;
	}

	public void setValidDataset(boolean validDataset) {
		this.validDataset = validDataset;
	}

}
