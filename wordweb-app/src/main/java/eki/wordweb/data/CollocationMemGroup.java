package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class CollocationMemGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String form;

	private List<Collocation> firstWordCollocations;

	private List<Collocation> lastWordCollocations;

	private List<Collocation> middleWordCollocations;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public List<Collocation> getFirstWordCollocations() {
		return firstWordCollocations;
	}

	public void setFirstWordCollocations(List<Collocation> firstWordCollocations) {
		this.firstWordCollocations = firstWordCollocations;
	}

	public List<Collocation> getLastWordCollocations() {
		return lastWordCollocations;
	}

	public void setLastWordCollocations(List<Collocation> lastWordCollocations) {
		this.lastWordCollocations = lastWordCollocations;
	}

	public List<Collocation> getMiddleWordCollocations() {
		return middleWordCollocations;
	}

	public void setMiddleWordCollocations(List<Collocation> middleWordCollocations) {
		this.middleWordCollocations = middleWordCollocations;
	}

}
