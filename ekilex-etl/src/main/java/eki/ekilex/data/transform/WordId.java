package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordId extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private List<Long> simWordIds;

	public WordId(Long wordId, List<Long> similarWordIds) {
		this.wordId = wordId;
		this.simWordIds = similarWordIds;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public List<Long> getSimWordIds() {
		return simWordIds;
	}

	public void setSimWordIds(List<Long> simWordIds) {
		this.simWordIds = simWordIds;
	}
}
