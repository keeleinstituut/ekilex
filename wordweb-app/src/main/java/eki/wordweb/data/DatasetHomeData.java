package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class DatasetHomeData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private DatasetStat dataset;

	private List<Character> firstLetters;

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(DatasetStat dataset) {
		this.dataset = dataset;
	}

	public List<Character> getFirstLetters() {
		return firstLetters;
	}

	public void setFirstLetters(List<Character> firstLetters) {
		this.firstLetters = firstLetters;
	}

}
