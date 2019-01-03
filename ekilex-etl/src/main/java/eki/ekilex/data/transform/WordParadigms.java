package eki.ekilex.data.transform;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordParadigms extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String wordValue;

	private List<Paradigm> paradigms;

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public List<Paradigm> getParadigms() {
		return paradigms;
	}

	public void setParadigms(List<Paradigm> paradigms) {
		this.paradigms = paradigms;
	}

}
