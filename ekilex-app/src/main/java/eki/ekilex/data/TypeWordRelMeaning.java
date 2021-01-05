package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TypeWordRelMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private List<String> definitions;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public List<String> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
	}
}
