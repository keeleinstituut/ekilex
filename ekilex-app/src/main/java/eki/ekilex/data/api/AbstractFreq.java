package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public abstract class AbstractFreq extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long freqCorpId;

	private Float value;

	private Long rank;

	public Long getFreqCorpId() {
		return freqCorpId;
	}

	public void setFreqCorpId(Long freqCorpId) {
		this.freqCorpId = freqCorpId;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

}
